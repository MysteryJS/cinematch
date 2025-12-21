document.getElementById('analyzeBtn').onclick = async function () {
    const text = document.getElementById('reviewText').value.trim();

    const resultDiv = document.getElementById('result');
    const genresDiv = document.getElementById('genresArea');
    const emotionsDiv = document.getElementById('emotionsArea');
    const titlesDiv = document.getElementById('titlesArea');

    genresDiv.innerHTML = '';
    emotionsDiv.innerHTML = '';
    titlesDiv.innerHTML = '';
    resultDiv.innerHTML = '';

    if (!text) {
        resultDiv.innerHTML = "<span class='error'>Write text</span>";
        return;
    }

    resultDiv.innerHTML = "Send to API...";

    try {
        const response = await fetch("https://mysterygre-cinematch-sentiment-analysis.hf.space/recommend", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ text })
        });

        if (!response.ok) {
            resultDiv.innerHTML = "<span class='error'>Error from API</span>";
            return;
        }

        let data;
        try {
            data = await response.json();
        } catch (parseErr) {
            resultDiv.innerHTML = "<span class='error'>The API returned unrecognizable JSON.</span>";
            return;
        }

        resultDiv.innerHTML = "";

        if (data.genres && Array.isArray(data.genres)) {
            genresDiv.innerHTML =
                "<strong>Genres:</strong> <ul>" +
                data.genres.map(g => `<li>${g}</li>`).join('') +
                "</ul>";
        } else {
            genresDiv.innerHTML = "<strong>Genres:</strong> —";
        }

        if (data.emotion_probabilities && typeof data.emotion_probabilities === 'object') {
            let rows = "";
            for (const [emotion, prob] of Object.entries(data.emotion_probabilities)) {
                rows += `<tr><td>${emotion}</td><td>${prob.toFixed(2)}%</td></tr>`;
            }
            emotionsDiv.innerHTML =
                `<strong>Emotion possibilities:</strong>
                <table class="emotions-table">
                  <thead><tr><th>Feeling</th><th>Chance</th></tr></thead>
                  <tbody>${rows}</tbody>
                </table>`;
        } else {
            emotionsDiv.innerHTML = "<strong>Probabilities:</strong> —";
        }

        function escapeHtml(unsafe) {
            if (unsafe === null || unsafe === undefined) return '';
            return String(unsafe)
                .replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&#039;');
        }

        if (data.titles && Array.isArray(data.titles)) {
            titlesDiv.innerHTML = "<strong>Detailed movie results:</strong>";
            const list = document.createElement("ul");
            list.className = 'movie-list';
            titlesDiv.appendChild(list);

            const movieDetails = await Promise.all(data.titles.map(async title => {
                try {
                    const searchRes = await fetch(`http://localhost:8080/api/movie/${encodeURIComponent(title)}`);
                    if (!searchRes.ok) return { title, details: null };
                    const details = await searchRes.json();
                    return { title, details };
                } catch (e) {
                    console.error('fetch movie error', title, e);
                    return { title, details: null };
                }
            }));

            movieDetails.forEach(({ title, details }) => {
                const li = document.createElement('li');
                li.className = 'movie-item';

                if (details && details.Title) {
                    let poster = details.Poster && details.Poster !== 'N/A' ? details.Poster : '/placeholder-poster.png';

                    li.innerHTML = `
        <div class="movie-card">
          <img class="movie-poster" src="${escapeHtml(poster)}" alt="${escapeHtml(details.Title)} poster"
               onerror="this.onerror=null;this.src='/placeholder-poster.png'">
          <div class="movie-meta">
            <h3 class="movie-title">${escapeHtml(details.Title || title)} ${details.Year ? '(' + escapeHtml(details.Year) + ')' : ''}</h3>
            <p><strong>Director:</strong> ${escapeHtml(details.Director || '—')}</p>
            <p><strong>Kind:</strong> ${escapeHtml(details.Genre || '—')}</p>
            <p><strong>IMDB:</strong> ${escapeHtml(details.imdbRating || '—')}</p>
            <p class="movie-plot">${escapeHtml(details.Plot || '')}</p>
          </div>
        </div>
      `;
                } else {
                    li.innerHTML = `<div class="movie-card"><div class="movie-meta"><h3>${escapeHtml(title)}</h3><p class="error">Δεν βρέθηκαν στοιχεία.</p></div></div>`;
                }

                list.appendChild(li);
            });
        } else {
            titlesDiv.innerHTML = "<strong>Movies:</strong> —";
        }

    } catch (err) {
        resultDiv.innerHTML = "<span class='error'>Error from API!</span>";
        console.error(err);
    }
};

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
            title: "Analysis & Suggestions API",
            desc: "Write your comment and get movie suggestions with emotions and genres!",
            placeholder: "Write here...",
            analyze: "Analyze Sentiment",
            clear: "Clear",
            tagline: "Discover movies with smart suggestions and short descriptions.",
            menu: "Menu", follow: "Follow us",
            rights: "All rights reserved.",
            tou: "Terms of Use",
            flag: "https://flagcdn.com/w40/gr.png", btnText: "GR"
        },
        el: {
            home: "Αρχική", search: "Αναζήτηση", trending: "Τάσεις",
            quiz: "Κουίζ", sentiment: "Ανάλυση Συναισθήματος", face: "Ανίχνευση Προσώπου",
            title: "API Ανάλυσης & Προτάσεων",
            desc: "Γράψτε το σχόλιό σας και λάβετε προτάσεις ταινιών με βάση τα συναισθήματα!",
            placeholder: "Γράψτε εδώ...",
            analyze: "Ανάλυση Συναισθήματος",
            clear: "Καθαρισμός",
            tagline: "Ανακαλύψτε ταινίες με έξυπνες προτάσεις και σύντομες περιγραφές.",
            menu: "Μενού", follow: "Ακολουθήστε μας",
            rights: "Με επιφύλαξη παντός δικαιώματος.",
            tou: "Όροι Χρήσης",
            flag: "https://flagcdn.com/w40/gb.png", btnText: "EN"
        }
    };

    let currentLang = "en";

    langBtn.addEventListener("click", () => {
        currentLang = currentLang === "en" ? "el" : "en";
        const t = translations[currentLang];

        const navLinks = document.querySelectorAll(".nav-link");
        navLinks[0].textContent = t.home;
        navLinks[1].textContent = t.search;
        navLinks[2].textContent = t.trending;
        navLinks[3].textContent = t.quiz;
        navLinks[4].textContent = t.sentiment;
        navLinks[5].textContent = t.face;

        document.querySelector(".page-header h2").textContent = t.title;
        document.querySelector(".page-header p").textContent = t.desc;
        document.getElementById("reviewText").placeholder = t.placeholder;
        document.getElementById("analyzeBtn").textContent = t.analyze;
        document.getElementById("clearBtn").textContent = t.clear;

        document.querySelector(".footer-tagline").textContent = t.tagline;
        document.querySelector(".footer-nav .footer-title").textContent = t.menu;
        document.querySelector(".footer-social .footer-title").textContent = t.follow;

        const footerLinks = document.querySelectorAll(".footer-menu .footer-link");
        footerLinks[0].textContent = t.home;
        footerLinks[1].textContent = t.search;
        footerLinks[2].textContent = t.trending;
        footerLinks[3].textContent = t.quiz;
        footerLinks[4].textContent = t.sentiment;
        footerLinks[5].textContent = t.face;

        document.querySelector(".footer-bottom .small").childNodes[2].textContent = " CineMatch — " + t.rights;
        document.querySelector(".footer-bottom a.footer-link").textContent = t.tou;

        langFlag.src = t.flag;
        langText.textContent = t.btnText;
    });
});