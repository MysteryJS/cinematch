const results = document.getElementById("trendingResults");

fetch("http://localhost:8080/api/trending")
    .then(response => response.json())
    .then(async (titles) => {
        if (!Array.isArray(titles) || titles.length === 0) {
            results.innerHTML = "<p class='error'>Δεν βρέθηκαν trending ταινίες.</p>";
            return;
        }

        const detailedMovies = await Promise.all(
            titles.map(async (title) => {
                try {
                    const omdbRes = await fetch(`http://localhost:8080/api/movie/${encodeURIComponent(title)}`);
                    return await omdbRes.json();
                } catch (err) {
                    console.log("OMDb error:", err);
                    return null;
                }
            })
        );

        results.innerHTML = detailedMovies.map(movie => {
            if (!movie || movie.Response === "False") {
                return `<div class="movie-card"><p>Σφάλμα φόρτωσης στοιχείων ταινίας.</p></div>`;
            }
            return `<div class="movie-card">
                <img src="${movie.Poster}" alt="Poster" class="poster-img">
                <div class="movie-info">
                    <h2>${movie.Title} (${movie.Year})</h2>
                    <p><strong>Ηθοποιοί:</strong> ${movie.Actors}</p>
                    <p><strong>Σκηνοθέτης:</strong> ${movie.Director}</p>
                    <p><strong>Είδος:</strong> ${movie.Genre}</p>
                    <p><strong>IMDB Rating:</strong> ⭐ ${movie.imdbRating}</p>
                    <p><strong>Πλοκή:</strong> ${movie.Plot}</p>
                </div>
            </div>`;
        }).join("");
    })
    .catch(err => {
        results.innerHTML = "<p class='error'>Σφάλμα επικοινωνίας με το backend!</p>";
        console.log(err);
    });


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
            quiz: "Quiz", sentiment: "Sentiment Analysis", face: "Face Detection", forum: "Forum",
            title: "Top 10 Trending Movies",
            desc: "The most popular movies right now from API",
            tagline: "Discover movies with smart suggestions and short descriptions.",
            menu: "Menu", follow: "Follow us", rights: "All rights reserved.", tou: "Terms of Use",
            flag: "https://flagcdn.com/w40/gr.png", btnText: "GR"
        },
        el: {
            home: "Αρχική", search: "Αναζήτηση", trending: "Τάσεις",
            quiz: "Κουίζ", sentiment: "Ανάλυση Συναισθήματος", face: "Ανίχνευση Προσώπου", forum: "Φόρουμ",
            title: "Οι 10 Κορυφαίες Ταινίες",
            desc: "Οι πιο δημοφιλείς ταινίες αυτή τη στιγμή μέσω API",
            tagline: "Ανακαλύψτε ταινίες με έξυπνες προτάσεις και σύντομες περιγραφές.",
            menu: "Μενού", follow: "Ακολουθήστε μας", rights: "Με επιφύλαξη παντός δικαιώματος.", tou: "Όροι Χρήσης",
            flag: "https://flagcdn.com/w40/gb.png", btnText: "EN"
        }
    };

    let currentLang = "en";

    langBtn.addEventListener("click", () => {
        currentLang = currentLang === "en" ? "el" : "en";
        const t = translations[currentLang];

        const navLinks = document.querySelectorAll(".nav-link");
        if (navLinks[0]) navLinks[0].textContent = t.home;
        if (navLinks[1]) navLinks[1].textContent = t.search;
        if (navLinks[2]) navLinks[2].textContent = t.trending;
        if (navLinks[3]) navLinks[3].textContent = t.quiz;
        if (navLinks[4]) navLinks[4].textContent = t.sentiment;
        if (navLinks[5]) navLinks[5].textContent = t.face;
        if (navLinks[6]) navLinks[6].textContent = t.forum;

        document.querySelector(".page-header h1").textContent = t.title;
        document.querySelector(".page-header p").textContent = t.desc;

        document.querySelector(".footer-tagline").textContent = t.tagline;

        const footerTitles = document.querySelectorAll(".footer-title");
        if (footerTitles[0]) footerTitles[0].textContent = t.menu;
        if (footerTitles[1]) footerTitles[1].textContent = t.follow;

        const footerLinks = document.querySelectorAll(".footer-menu .footer-link");
        if (footerLinks[0]) footerLinks[0].textContent = t.home;
        if (footerLinks[1]) footerLinks[1].textContent = t.search;
        if (footerLinks[2]) footerLinks[2].textContent = t.trending;
        if (footerLinks[3]) footerLinks[3].textContent = t.quiz;
        if (footerLinks[4]) footerLinks[4].textContent = t.sentiment;
        if (footerLinks[5]) footerLinks[5].textContent = t.face;
        if (footerLinks[6]) footerLinks[6].textContent = t.forum;

        const footerBottom = document.querySelector(".footer-bottom .small");
        if (footerBottom) {
            footerBottom.innerHTML = `&copy; <span id="year">2025</span> CineMatch — ${t.rights}`;
        }

        const touLink = document.querySelector(".footer-bottom a.footer-link");
        if (touLink) touLink.textContent = t.tou;

        langFlag.src = t.flag;
        langText.textContent = t.btnText;
    });
});
})();
