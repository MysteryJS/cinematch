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

/* ------------------ RATING (Stars) ------------------ */

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
            // Αν αποτύχει, απλώς μην “σπάσεις” το UI
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
                alert('Πρέπει να είσαι συνδεδεμένος για να ψηφίσεις.');
                return;
            }

            await loadRatingSummary(ratingEl, postId);
        });
    }
}

/* ---------------------------------------------------- */

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
        document.getElementById('postsEmpty').textContent = 'Αποτυχία φόρτωσης posts.';
        return;
    }

    const posts = await res.json();

    const emptyEl = document.getElementById('postsEmpty');
    const container = document.getElementById('postsContainer');
    container.innerHTML = '';

    if (!posts || posts.length === 0) {
        emptyEl.style.display = 'block';
        emptyEl.textContent = 'Δεν υπάρχουν posts σε αυτή την κατηγορία.';
        return;
    }

    emptyEl.style.display = 'none';

    for (const p of posts) {
        const card = document.createElement('div');
        card.className = 'movie-card';

        const createdAt = p.createdAt ? new Date(p.createdAt).toLocaleString('el-GR') : '—';

        card.innerHTML = `
                <div class="movie-title"></div>
                <div class="text-muted small"></div>
                <p></p>
            `;

        card.querySelector('.movie-title').textContent = p.title ?? '';
        card.querySelector('.text-muted').textContent = `Χρήστης: ${p.username ?? 'unknown'} | Ημερομηνία: ${createdAt}`;
        card.querySelector('p').textContent = p.content ?? '';

        appendMedia(card, p.mediaUrls);

        // --- add rating block κάτω από το post ---
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
                throw new Error(`Το video "${f.name}" είναι ${Math.ceil(dur)}s. Επιτρέπονται έως 60s.`);
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
        alert(text || 'Αποτυχία δημοσίευσης.');
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
        alert('Το post δημιουργήθηκε, αλλά απέτυχε το upload των media: ' + e.message);
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
