document.addEventListener("DOMContentLoaded", function () {
    const langBtn = document.getElementById("lang-btn");
    const langFlag = document.getElementById("lang-flag");
    const langText = document.getElementById("lang-text");

    const translations = {
        en: {
            home: "Home",
            search: "Search",
            trending: "Trending Movies",
            quiz: "Quiz",
            sentiment: "Sentiment Analysis",
            face: "Face Detection",
            forum: "Forum",
            welcome: "Welcome to CineMatch!",
            subtitle: "Discover movies with AI-Powered tools",
            tagline: "Discover movies with smart suggestions and short descriptions.",
            menu: "Menu",
            follow: "Follow us",
            rights: "All rights reserved.",
            designed: "Designed with ❤ for movie lovers.",
            tou: "Terms Of Use",
            flag: "🇬🇧", btnText: "EN"
        },
        el: {
            home: "Αρχική",
            search: "Αναζήτηση",
            trending: "Τάσεις",
            quiz: "Κουίζ",
            sentiment: "Ανάλυση Συναισθήματος",
            face: "Ανίχνευση Προσώπου",
            forum: "Φόρουμ",
            welcome: "Καλώς ήρθατε στο CineMatch!",
            subtitle: "Ανακαλύψτε ταινίες με AI εργαλεία",
            tagline: "Ανακαλύψτε ταινίες με έξυπνες προτάσεις και σύντομες περιγραφές.",
            menu: "Μενού",
            follow: "Ακολουθήστε μας",
            rights: "Με επιφύλαξη παντός δικαιώματος.",
            designed: "Σχεδιάστηκε με ❤ για τους λάτρεις του σινεμά.",
            tou: "Όροι Χρήσης",
            flag: "🇬🇷", btnText: "GR"
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

        document.querySelector(".hero-main-title").textContent = t.welcome;
        document.querySelector(".hero-subtitle").textContent = t.subtitle;

        const footerLinks = document.querySelectorAll(".footer-menu .footer-link");
        footerLinks[0].textContent = t.home;
        footerLinks[1].textContent = t.search;
        footerLinks[2].textContent = t.trending;
        footerLinks[3].textContent = t.quiz;
        footerLinks[4].textContent = t.sentiment;
        footerLinks[5].textContent = t.face;

        document.querySelector(".footer-tagline").textContent = t.tagline;
        document.querySelector(".footer-nav .footer-title").textContent = t.menu;
        document.querySelector(".footer-social .footer-title").textContent = t.follow;
        document.getElementById("footer-rights").textContent = t.rights;
        document.getElementById("footer-designed").textContent = t.designed;
        document.getElementById("footer-tou").textContent = t.tou;

        langFlag.textContent = t.flag;
        langText.textContent = t.btnText;
    });

    const homeSection = document.getElementById("home");
    const factBox = document.createElement("div");
    factBox.id = "fun-fact";
    homeSection.appendChild(factBox);

    fetch("https://opentdb.com/api.php?amount=1&category=11&type=multiple")
        .then(response => response.json())
        .then(data => {
            if (data.results.length > 0) {
                const q = data.results[Math.floor(Math.random() * data.results.length)];
                const decode = s => {
                    const txt = document.createElement("textarea");
                    txt.innerHTML = s;
                    return txt.value;
                };
                factBox.innerHTML = `<strong>Did you know?</strong> ${decode(q.question)} <br><b>${decode(q.correct_answer)}</b>`;
            }
        })
        .catch(() => {
            factBox.textContent = "Δεν ήταν δυνατή η ανάκτηση fun facts.";
        });
});