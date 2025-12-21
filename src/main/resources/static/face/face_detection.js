const dropzone = document.getElementById('dropzone');
const photoInput = document.getElementById('photoInput');
const chooseBtn = document.getElementById('chooseBtn');
const sendBtn = document.getElementById('sendBtn');
const clearBtn = document.getElementById('clearBtn');
const preview = document.getElementById('preview');
const previewImg = document.getElementById('previewImg');
const results = document.getElementById('results');

let currentFile = null;

['dragenter', 'dragover'].forEach(evt => {
    dropzone.addEventListener(evt, (e) => {
        e.preventDefault();
        dropzone.classList.add('dz-over');
    });
});
['dragleave', 'drop', 'dragend'].forEach(evt => {
    dropzone.addEventListener(evt, (e) => {
        e.preventDefault();
        dropzone.classList.remove('dz-over');
    });
});

dropzone.addEventListener('click', () => photoInput.click());
dropzone.addEventListener('drop', (e) => {
    const dt = e.dataTransfer;
    if (dt && dt.files && dt.files[0]) handleFile(dt.files[0]);
});

photoInput.addEventListener('change', (e) => {
    if (e.target.files && e.target.files[0]) handleFile(e.target.files[0]);
});

chooseBtn.addEventListener('click', () => photoInput.click());
clearBtn.addEventListener('click', clearSelection);

sendBtn.addEventListener('click', () => {
    if (!currentFile) return;
    analyzePhoto(currentFile);
});

function handleFile(file) {
    const maxMB = 8;
    if (!file.type.startsWith('image/')) {
        showError('Please select an image file.');
        return;
    }
    if (file.size > maxMB * 1024 * 1024) {
        showError(`The file is too large. Maximum ${maxMB}MB.`);
        return;
    }

    currentFile = file;
    const url = URL.createObjectURL(file);
    previewImg.src = url;
    previewImg.hidden = false;
    preview.querySelector('.preview-empty').hidden = true;

    sendBtn.disabled = false;
    clearBtn.disabled = false;
    results.innerHTML = '<div class="results-empty">Ready for analysis. Click "Analysis &amp; Compare".</div>';
}

function clearSelection() {
    currentFile = null;
    previewImg.src = '';
    previewImg.hidden = true;
    preview.querySelector('.preview-empty').hidden = false;
    sendBtn.disabled = true;
    clearBtn.disabled = true;
    results.innerHTML = '<div class="results-empty">The results of the comparison will appear here.</div>';
}

function showError(message) {
    results.innerHTML = `<div class="error">${escapeHtml(message)}</div>`;
}

function showLoading() {
    results.innerHTML = `<div class="loading">Image analysis…</div>`;
}

async function getActorImage(actorName) {
    const cleanName = actorName.replace(/_/g, ' ');
    
    try {
        // 1η Προσπάθεια: TMDB API
        const tmdbRes = await fetch(`https://api.themoviedb.org/3/search/person?api_key=15d1215d0473846104031f8b3617029b&query=${encodeURIComponent(cleanName)}`);
        const tmdbData = await tmdbRes.json();

        if (tmdbData.results && tmdbData.results.length > 0 && tmdbData.results[0].profile_path) {
            return `https://image.tmdb.org/t/p/w500${tmdbData.results[0].profile_path}`;
        }

        const wikiRes = await fetch(`https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(cleanName.replace(/\b\w/g, l => l.toUpperCase()).replace(/ /g, '_'))}`);
        const wikiData = await wikiRes.json();
        
        if (wikiData.thumbnail && wikiData.thumbnail.source) {
            return wikiData.thumbnail.source;
        }

    } catch (err) {
        console.error("Error fetching image:", err);
    }
    return null;
}

async function renderResults(data) {
    if (data.error) {
        results.innerHTML = `<div class="no-match">${escapeHtml(data.error)}</div>`;
        return;
    }

    let similarity = data.similarity;
    if (similarity === undefined && data.score !== undefined) {
        similarity = (1 - data.score) * 100;
    }

    if (similarity !== undefined && similarity < 15) {
        results.innerHTML = `<div class="no-match">No close enough person was found (similarity only ${Math.round(similarity)}%).</div>`;
        return;
    }

    if (data.closest_celebrity) {
        const actorNameRaw = data.closest_celebrity;
        const actorNameDisplay = actorNameRaw.replace(/_/g, ' ');
        
        showLoading(); 
        const imageUrl = await getActorImage(actorNameRaw);

        results.innerHTML = `
            <div class="result-card" style="text-align: center; padding: 20px;">
                <h3 style="margin-bottom: 15px;">Result</h3>
                
                ${imageUrl ? `
                    <div class="match-photo" style="margin-bottom: 15px;">
                        <img src="${imageUrl}" alt="${escapeHtml(actorNameDisplay)}" 
                             style="width: 150px; height: 150px; border-radius: 50%; object-fit: cover; border: 3px solid var(--primary); display: block; margin: 0 auto;">
                    </div>` 
                : '<p style="color: var(--muted);">Photo not found</p>'}
                
                <div class="match-main">
                    <p style="font-size: 1.2rem; margin: 5px 0;">
                        <strong>Actor:</strong> 
                        <span style="text-transform: capitalize; color: var(--primary);">${escapeHtml(actorNameDisplay)}</span>
                    </p>
                    <p class="similarity" style="font-size: 1.1rem;">
                        <strong>Similarity:</strong> ${Math.round(similarity)}%
                    </p>
                </div>
            </div>
        `;
        return;
    }
    results.innerHTML = `<div class="no-match">No result found..</div>`;
}

async function analyzePhoto(file) {
    showLoading();
    const fd = new FormData();
    fd.append('photo', file, file.name);

    try {
        const res = await fetch('https://mysterygre-cinematch-face-detection.hf.space/match', {
            method: 'POST',
            body: fd
        });
        const json = await res.json();
        await renderResults(json);
    } catch (err) {
        showError('Network error: ' + (err && err.message ? err.message : err));
    }
}

function escapeHtml(unsafe) {
    if (!unsafe && unsafe !== '') return '';
    return String(unsafe)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

(function updateFooterYear() {
    const y = new Date().getFullYear();
    const el = document.getElementById('year');
    if (el) el.textContent = y;
})();

document.addEventListener("DOMContentLoaded", function () {
    const langBtn = document.getElementById("lang-btn");
    const langFlag = document.getElementById("lang-flag");
    const langText = document.getElementById("lang-text");

    const translations = {
        en: {
            home: "Home", search: "Search", trending: "Trending Movies",
            quiz: "Quiz", sentiment: "Sentiment Analysis", face: "Face Detection",
            title: "Find out which actor looks like you",
            desc: "Upload a photo and we'll show you the closest actor and other suggestions.",
            dropzone: "Click or drag a photo here",
            choose: "Select file",
            send: "Analysis & Comparison",
            clear: "Cleaning",
            results: "The results of the comparison will appear here..",
            tagline: "Discover movies with smart suggestions and short descriptions.",
            menu: "Menu", follow: "Follow us",
            rights: "All rights reserved.",
            tou: "Terms of Use",
            flag: "🇬🇷", btnText: "GR"
        },
        el: {
            home: "Αρχική", search: "Αναζήτηση", trending: "Τάσεις",
            quiz: "Κουίζ", sentiment: "Ανάλυση Συναισθήματος", face: "Ανίχνευση Προσώπου",
            title: "Μάθετε με ποιον ηθοποιό μοιάζετε",
            desc: "Ανεβάστε μια φωτογραφία και θα σας δείξουμε τον πλησιέστερο ηθοποιό και άλλες προτάσεις.",
            dropzone: "Κάντε κλικ ή σύρετε μια φωτογραφία εδώ",
            choose: "Επιλογή αρχείου",
            send: "Ανάλυση & Σύγκριση",
            clear: "Καθαρισμός",
            results: "Τα αποτελέσματα της σύγκρισης θα εμφανιστούν εδώ..",
            tagline: "Ανακαλύψτε ταινίες με έξυπνες προτάσεις και σύντομες περιγραφές.",
            menu: "Μενού", follow: "Ακολουθήστε μας",
            rights: "Με επιφύλαξη παντός δικαιώματος.",
            tou: "Όροι Χρήσης",
            flag: "🇬🇧", btnText: "EN"
        }
    };

    let currentLang = "en";

    langBtn.addEventListener("click", () => {
        currentLang = currentLang === "en" ? "el" : "en";
        const t = translations[currentLang];

        const navLinks = document.querySelectorAll(".nav-link");
        if(navLinks[0]) navLinks[0].textContent = t.home;
        if(navLinks[1]) navLinks[1].textContent = t.search;
        if(navLinks[2]) navLinks[2].textContent = t.trending;
        if(navLinks[3]) navLinks[3].textContent = t.quiz;
        if(navLinks[4]) navLinks[4].textContent = t.sentiment;
        if(navLinks[5]) navLinks[5].textContent = t.face;

        document.querySelector(".page-header h1").textContent = t.title;
        document.querySelector(".page-header p").textContent = t.desc;
        document.querySelector(".dz-content p").textContent = t.dropzone;
        document.getElementById("chooseBtn").textContent = t.choose;
        document.getElementById("sendBtn").textContent = t.send;
        document.getElementById("clearBtn").textContent = t.clear;

        const resultsEmpty = document.querySelector(".results-empty");
        if (resultsEmpty) resultsEmpty.textContent = t.results;

        document.querySelector(".footer-tagline").textContent = t.tagline;
        document.querySelector(".footer-nav .footer-title").textContent = t.menu;
        document.querySelector(".footer-social .footer-title").textContent = t.follow;

        const footerLinks = document.querySelectorAll(".footer-menu .footer-link");
        if(footerLinks[0]) footerLinks[0].textContent = t.home;
        if(footerLinks[1]) footerLinks[1].textContent = t.search;
        if(footerLinks[2]) footerLinks[2].textContent = t.trending;
        if(footerLinks[3]) footerLinks[3].textContent = t.quiz;
        if(footerLinks[4]) footerLinks[4].textContent = t.sentiment;
        if(footerLinks[5]) footerLinks[5].textContent = t.face;

        document.querySelector(".footer-bottom .small").childNodes[2].textContent = " CineMatch — " + t.rights;
        document.querySelector(".footer-bottom a.footer-link").textContent = t.tou;

        langFlag.textContent = t.flag;
        langText.textContent = t.btnText;
    });
});
