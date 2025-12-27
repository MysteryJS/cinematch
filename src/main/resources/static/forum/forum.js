let selectedCategory = null;

function appendMedia(card, mediaUrls) {
    if (!mediaUrls || mediaUrls.length === 0) return;

    const mediaWrap = document.createElement('div');
    mediaWrap.style.marginTop = '10px';

    for (const url of mediaUrls) {
        if (!url) continue;

        if (url.includes('/uploads/images/')) {
            const img = document.createElement('img');
            img.src = url;
            img.style.maxWidth = '400px';
            img.style.display = 'block';
            img.style.marginTop = '8px';
            mediaWrap.appendChild(img);
            continue;
        }

        if (url.includes('/uploads/videos/')) {
            const video = document.createElement('video');
            video.controls = true;
            video.style.maxWidth = '400px';
            video.style.display = 'block';
            video.style.marginTop = '8px';

            const source = document.createElement('source');
            source.src = url;
            video.appendChild(source);

            mediaWrap.appendChild(video);
        }
    }

    card.appendChild(mediaWrap);
}

function createRatingBlock(postId) {
    const wrap = document.createElement('div');
    wrap.className = 'post-rating';
    wrap.setAttribute('data-post-id', String(postId));

    wrap.innerHTML = `
        <div class="stars" aria-label="Rate this post">
            <span class="star" data-value="1">★</span>
            <span class="star" data-value="2">★</span>
            <span class="star" data-value="3">★</span>
            <span class="star" data-value="4">★</span>
            <span class="star" data-value="5">★</span>
        </div>
        <div class="rating-meta">
            <span class="avg">0.0</span>/5 • <span class="count">0</span> votes
        </div>
    `;
    return wrap;
}

function setStars(ratingEl, value) {
    const stars = ratingEl.querySelectorAll('.star');
    for (let i = 0; i < stars.length; i++) {
        const v = parseInt(stars[i].getAttribute('data-value'), 10);
        if (v <= value) stars[i].classList.add('active');
        else stars[i].classList.remove('active');
    }
}

async function loadRatingSummary(ratingEl, postId) {
    try {
        const res = await fetch(`/api/forum/posts/${postId}/rating`);
        if (!res.ok) {
            // If it fails, just don’t break the UI
            return;
        }

        const data = await res.json();
        const avg = typeof data.average === 'number' ? data.average : 0.0;
        const cnt = typeof data.count === 'number' ? data.count : 0;

        ratingEl.querySelector('.avg').textContent = (Math.round(avg * 10) / 10).toFixed(1);
        ratingEl.querySelector('.count').textContent = String(cnt);

        const my = data.myRating ? data.myRating : 0;
        setStars(ratingEl, my);
    } catch (e) {
        // ignore
    }
}

function wireRating(ratingEl) {
    const postId = ratingEl.getAttribute('data-post-id');

    loadRatingSummary(ratingEl, postId);

    const stars = ratingEl.querySelectorAll('.star');
    for (let i = 0; i < stars.length; i++) {
        stars[i].addEventListener('click', async function () {
            const v = parseInt(this.getAttribute('data-value'), 10);

            const res = await fetch(`/api/forum/posts/${postId}/rating`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ rating: v })
            });

            if (!res.ok) {
                alert('You must be logged in to vote.');
                return;
            }

            await loadRatingSummary(ratingEl, postId);
        });
    }
}

async function uploadFilesForPost(postId, files) {
    for (const file of files) {
        const formData = new FormData();
        formData.append('file', file);

        const res = await fetch(`/forum/media/${postId}`, {
            method: 'POST',
            body: formData
        });

        if (!res.ok) {
            const text = await res.text();
            throw new Error(text || 'Upload failed');
        }
    }
}

async function loadPosts() {
    let url = '/api/forum/posts';
    if (selectedCategory) {
        url += '?category=' + encodeURIComponent(selectedCategory);
    }

    const res = await fetch(url);
    if (!res.ok) {
        document.getElementById('postsEmpty').style.display = 'block';
        document.getElementById('postsEmpty').textContent = 'Failed to load posts.';
        return;
    }

    const posts = await res.json();

    const emptyEl = document.getElementById('postsEmpty');
    const container = document.getElementById('postsContainer');
    container.innerHTML = '';

    if (!posts || posts.length === 0) {
        emptyEl.style.display = 'block';
        emptyEl.textContent = 'No posts in this category.';
        return;
    }

    emptyEl.style.display = 'none';

    for (const p of posts) {
        const card = document.createElement('div');
        card.className = 'movie-card';

        const createdAt = p.createdAt ? new Date(p.createdAt).toLocaleString('en-GB') : '—';

        card.innerHTML = `
                <div class="movie-title"></div>
                <div class="text-muted small"></div>
                <p></p>
            `;

        card.querySelector('.movie-title').textContent = p.title ?? '';
        card.querySelector('.text-muted').textContent = `User: ${p.username ?? 'unknown'} | Date: ${createdAt}`;
        card.querySelector('p').textContent = p.content ?? '';

        appendMedia(card, p.mediaUrls);

        const ratingBlock = createRatingBlock(p.id);
        card.appendChild(ratingBlock);
        wireRating(ratingBlock);

        container.appendChild(card);
    }
}

function selectCategory(category) {
    selectedCategory = category;
    loadPosts();
}

async function getVideoDurationSeconds(file) {
    return await new Promise((resolve, reject) => {
        const url = URL.createObjectURL(file);
        const v = document.createElement('video');
        v.preload = 'metadata';
        v.onloadedmetadata = () => {
            const d = v.duration;
            URL.revokeObjectURL(url);
            if (!isFinite(d)) reject(new Error('Cannot read video duration'));
            else resolve(d);
        };
        v.onerror = () => {
            URL.revokeObjectURL(url);
            reject(new Error('Cannot read video'));
        };
        v.src = url;
    });
}

async function validateSelectedFiles(files) {
    const out = [];
    for (const f of files) {
        if (f.type && f.type.startsWith('video/')) {
            const dur = await getVideoDurationSeconds(f);
            if (dur > 60) {
                throw new Error(`The video "${f.name}" is ${Math.ceil(dur)}s. Maximum allowed is 60s.`);
            }
        }
        out.push(f);
    }
    return out;
}

function renderSelectedMediaPreview(files) {
    const wrap = document.getElementById('mediaPreview');
    if (!wrap) return;

    wrap.innerHTML = '';

    if (!files || files.length === 0) return;

    for (const f of files) {
        const row = document.createElement('div');
        row.className = 'text-muted small';
        row.style.marginTop = '6px';
        row.textContent = `${f.name} (${Math.ceil(f.size / 1024)} KB)`;
        wrap.appendChild(row);

        if (f.type && f.type.startsWith('image/')) {
            const img = document.createElement('img');
            img.src = URL.createObjectURL(f);
            img.style.maxWidth = '200px';
            img.style.display = 'block';
            img.style.marginTop = '6px';
            wrap.appendChild(img);
        }
    }
}

async function submitPost() {
    const titleEl = document.getElementById('postTitle');
    const contentEl = document.getElementById('postContent');
    const mediaEl = document.getElementById('postMedia');

    const title = titleEl.value.trim();
    const content = contentEl.value.trim();

    if (!title || !content) return;

    const res = await fetch('/api/forum/post', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title: title,
            content: content,
            category: selectedCategory || 'General'
        })
    });

    if (!res.ok) {
        const text = await res.text();
        alert(text || 'Failed to post.');
        return;
    }

    const createdPost = await res.json();
    const postId = createdPost.id;

    const rawFiles = mediaEl && mediaEl.files ? Array.from(mediaEl.files) : [];
    let files = [];

    try {
        files = await validateSelectedFiles(rawFiles);
    } catch (e) {
        alert(e.message);
        return;
    }

    try {
        if (files.length > 0) {
            await uploadFilesForPost(postId, files);
        }
    } catch (e) {
        alert('The post was created, but uploading media failed: ' + e.message);
    }

    titleEl.value = '';
    contentEl.value = '';
    if (mediaEl) mediaEl.value = '';
    renderSelectedMediaPreview([]);

    await loadPosts();
}

document.addEventListener('DOMContentLoaded', () => {
    loadPosts();

    document.querySelectorAll('.kpi-box[data-category]').forEach(btn => {
        btn.addEventListener('click', () => selectCategory(btn.dataset.category));
    });

    const mediaEl = document.getElementById('postMedia');
    if (mediaEl) {
        mediaEl.addEventListener('change', async () => {
            const rawFiles = Array.from(mediaEl.files || []);
            try {
                await validateSelectedFiles(rawFiles);
                renderSelectedMediaPreview(rawFiles);
            } catch (e) {
                alert(e.message);
                mediaEl.value = '';
                renderSelectedMediaPreview([]);
            }
        });
    }

    document.getElementById('submitPostBtn').addEventListener('click', submitPost);
});

document.addEventListener("DOMContentLoaded", function () {
    const langBtn = document.getElementById("lang-btn");
    if (!langBtn) return;
    const langFlag = document.getElementById("lang-flag");
    const langText = document.getElementById("lang-text");

    const translations = {
        en: {
            general: "General", forum: "Forum", categories: "Categories", latest: "Latest Discussions", create: "Create new topic",
            topicTitle: "Topic title", yourMsg: "Your message...", postBtn: "Post",
            empty: "No posts yet.", none: "No posts in this category.", user: "User", date: "Date",
            horror: "Horror Movies", comedy: "Comedies", drama: "Drama", adventure: "Adventure", series: "Series",
            suggestions: "User Suggestions",
            rating: "votes", failed: "Failed to load posts.", genCat: "General",
            footerTag: "Discover movies with smart suggestions and short descriptions.",
            menu: "Menu", home: "Home", search: "Search", trending: "Trending Movies", quiz: "Quiz",
            sentiment: "Sentiment Analysis", face: "Face Detection",
            login: "Login", logout: "Logout", follow: "Follow us",
            allRights: "All rights reserved.", tou: "Terms of Use", designed: "Designed with ❤ for movie lovers.",
            imgErr: "The video is too long. Maximum allowed is 60s.",
            flag: "🇬🇷", btnText: "GR"
        },
        el: {
            general: "Γενικά", forum: "Forum", categories: "Κατηγορίες", latest: "Τελευταίες Συζητήσεις", create: "Δημιούργησε νέο θέμα",
            topicTitle: "Τίτλος θέματος", yourMsg: "Το μήνυμά σας...", postBtn: "Δημοσίευση",
            empty: "Δεν υπάρχουν posts ακόμα.", none: "Δεν υπάρχουν posts σε αυτή την κατηγορία.", user: "Χρήστης", date: "Ημερομηνία",
            horror: "Ταινίες Τρόμου", comedy: "Κωμωδίες", drama: "Δράμα", adventure: "Περιπέτεια", series: "Σειρές",
            suggestions: "Προτάσεις Χρηστών",
            rating: "ψήφοι", failed: "Αποτυχία φόρτωσης posts.", genCat: "Γενικά",
            footerTag: "Ανακαλύψτε ταινίες με έξυπνες προτάσεις και σύντομες περιγραφές.",
            menu: "Μενού", home: "Αρχική", search: "Αναζήτηση", trending: "Τάσεις", quiz: "Κουίζ",
            sentiment: "Ανάλυση Συναισθήματος", face: "Ανίχνευση Προσώπου",
            login: "Σύνδεση", logout: "Αποσύνδεση", follow: "Ακολουθήστε μας",
            allRights: "Με επιφύλαξη παντός δικαιώματος.", tou: "Όροι Χρήσης", designed: "Designed with ❤ for movie lovers.",
            imgErr: "Το video είναι πολύ μεγάλο. Μέγιστο επιτρεπόμενο 60s.",
            flag: "🇬🇧", btnText: "EN"
        }
    };

    let currentLang = "el";
    langBtn.addEventListener("click", () => {
        currentLang = currentLang === "el" ? "en" : "el";
        const t = translations[currentLang];

        document.querySelectorAll(".nav-link")[0].textContent = t.home;
        document.querySelectorAll(".nav-link")[1].textContent = t.search;
        document.querySelectorAll(".nav-link")[2].textContent = t.trending;
        document.querySelectorAll(".nav-link")[3].textContent = t.quiz;
        document.querySelectorAll(".nav-link")[4].textContent = t.sentiment;
        document.querySelectorAll(".nav-link")[5].textContent = t.face;
        document.querySelectorAll(".nav-link")[6].textContent = t.forum;

        if (document.querySelector('a[href="/login"]')) document.querySelector('a[href="/login"]').textContent = t.login;
        if (document.querySelector('a[href="/logout"]')) document.querySelector('a[href="/logout"]').textContent = t.logout;

        document.querySelector(".categories h2").textContent = t.categories;
        const catBoxes = document.querySelectorAll('.kpi-box[data-category]');
        if (catBoxes.length >= 7) {
            catBoxes[0].textContent = t.general;
            catBoxes[1].textContent = t.horror;
            catBoxes[2].textContent = t.comedy;
            catBoxes[3].textContent = t.drama;
            catBoxes[4].textContent = t.adventure;
            catBoxes[5].textContent = t.series;
            catBoxes[6].textContent = t.suggestions;
        }

        document.querySelector(".posts h2").textContent = t.latest;
        document.getElementById("postsEmpty").textContent = t.empty;

        document.querySelector(".search-container h2").textContent = t.create;
        document.getElementById("postTitle").placeholder = t.topicTitle;
        document.getElementById("postContent").placeholder = t.yourMsg;
        document.getElementById("submitPostBtn").textContent = t.postBtn;

        document.querySelector(".footer-tagline").textContent = t.footerTag;
        document.querySelector(".footer-nav .footer-title").textContent = t.menu;
        document.querySelector(".footer-social .footer-title").textContent = t.follow;

        const fLinks = document.querySelectorAll(".footer-menu .footer-link");
        if (fLinks.length >= 7) {
            fLinks[0].textContent = t.home;
            fLinks[1].textContent = t.search;
            fLinks[2].textContent = t.trending;
            fLinks[3].textContent = t.quiz;
            fLinks[4].textContent = t.sentiment;
            fLinks[5].textContent = t.face;
            fLinks[6].textContent = t.forum;
        }
        const fb = document.querySelector(".footer-bottom .small");
        if (fb) fb.innerHTML = `&copy; <span id="year">${new Date().getFullYear()}</span> CineMatch — ${t.allRights}`;
        document.querySelector('.footer-bottom .footer-link').textContent = t.tou;

        langFlag.textContent = t.flag;
        langText.textContent = t.btnText;

        window.FORUM_LANG = currentLang;
    });

    window.FORUM_LANG = currentLang;
});

