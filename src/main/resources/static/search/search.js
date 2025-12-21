const searchBtn = document.getElementById("searchBtn");
const input = document.getElementById("searchInput");
const results = document.getElementById("movieResults");

searchBtn.addEventListener("click", () => {
  const title = input.value.trim();
  if (!title) {
    results.innerHTML = "<p class='error'>Write a movie first.</p>";
    return;
  }

  document.getElementById('movieResults').innerHTML = '';
  document.getElementById('kpiResults').innerHTML = '';

  fetch(`http://localhost:8080/api/movie/search?title=${encodeURIComponent(title)}`)
    .then(res => res.json())
    .then(data => {
      console.log(data);

      if (data.Response === "False") {
        results.innerHTML = "<p class='error'>No movie found.</p>";
        return;
      }
      results.innerHTML = `
                <div class="movie-card">
                    <img src="${data.Poster}" alt="Poster">
                    <div class="movie-info">
                        <h2>${data.Title} (${data.Year})</h2>
                        <p><strong>Actors:</strong> ${data.Actors}</p>
                        <p><strong>Director:</strong> ${data.Director}</p>
                        <p><strong>Kind:</strong> ${data.Genre}</p>
                        <p><strong>IMDB Rating:</strong> 🌟 ${data.imdbRating}</p>
                        <p><strong>Plot:</strong> ${data.Plot}</p>
                    </div>
                </div>`;

      fetch(`http://localhost:8080/api/kpi/movie/boxoffice/${data.imdbID}`)
        .then(res => res.json())
        .then(boxoffice => {
          document.getElementById('kpiResults').innerHTML +=
            `<div class="kpi-box">
        <h4>Box Office Proxy</h4>
        <p>${boxoffice}</p>
      </div>`;
        });

      fetch(`http://localhost:8080/api/kpi/movie/awards/${data.imdbID}`)
        .then(res => res.json())
        .then(awards => {
          document.getElementById('kpiResults').innerHTML +=
            `<div class="kpi-box">
        <h4>Awards Potential</h4>
        <p>${awards}</p>
      </div>`;
        });

      const mainActor = data.Actors && data.Actors.split(",")[0].trim();
      if (mainActor) {
        fetch(`http://localhost:8080/api/kpi/actor/starpower/${encodeURIComponent(mainActor)}`)
          .then(res => res.json())
          .then(starpower => {
            document.getElementById('kpiResults').innerHTML +=
              `<div class="kpi-box">
          <h4>Star Power (${mainActor})</h4>
          <p>${starpower}</p>
        </div>`;
          });
      }

      fetch(`http://localhost:8080/api/kpi/quiz/audience`)
        .then(res => res.json())
        .then(audience => {
          document.getElementById('kpiResults').innerHTML +=
            `<div class="kpi-box">
        <h4>Audience Engagement (Quiz)</h4>
        <p>${audience}</p>
      </div>`;
        });

    })
    .catch(err => {
      results.innerHTML = "<p class='error'>Connection error.</p>";
    });
});

input.addEventListener("keydown", function (event) {
  if (event.key === "Enter") {
    event.preventDefault();
    searchBtn.click();
  }
});

(function updateFooterYear() {
  const y = new Date().getFullYear();
  const el = document.getElementById('year');
  if (el) el.textContent = y;
})();

const historyBtn = document.getElementById("historyBtn");
const historyContainer = document.getElementById("historyContainer");

historyBtn.addEventListener("click", () => {
  fetch("/api/history/mine")
    .then(res => {
      if (!res.ok) throw new Error();
      return res.json();
    })
    .then(data => {
      historyContainer.innerHTML = "";

      if (data.length === 0) {
        historyContainer.innerHTML = "<p>There is no history.</p>";
        return;
      }

      data.forEach(item => {
        const div = document.createElement("div");
        const date = new Date(item.searchedAt).toLocaleString("el-GR");
        div.innerHTML = `
      <p><strong>Movie:</strong> ${item.movieTitle ?? item.movieId}</p>
      <p><small>${date}</small></p>
    `;
        historyContainer.appendChild(div);
      });
    }).catch(() => alert("You must be logged in."));
});

document.addEventListener("DOMContentLoaded", function () {
  const langBtn = document.getElementById("lang-btn");
  const langFlag = document.getElementById("lang-flag");
  const langText = document.getElementById("lang-text");

  const translations = {
    en: {
      home: "Home", search: "Search", trending: "Trending Movies",
      quiz: "Quiz", sentiment: "Sentiment Analysis", face: "Face Detection",
      login: "Login", logout: "Logout",
      title: "Movie Search",
      desc: "Type in a movie and see details from the OMDb database.",
      placeholder: "Type movie title...",
      searchBtn: "Search",
      historyBtn: "Show my history",
      tagline: "Discover movies with smart suggestions and short descriptions.",
      menu: "Menu", follow: "Follow us",
      rights: "All rights reserved.",
      tou: "Terms of Use",
      flag: "https://flagcdn.com/w40/gr.png", btnText: "GR"
    },
    el: {
      home: "Αρχική", search: "Αναζήτηση", trending: "Τάσεις",
      quiz: "Κουίζ", sentiment: "Ανάλυση Συναισθήματος", face: "Ανίχνευση Προσώπου",
      login: "Σύνδεση", logout: "Αποσύνδεση",
      title: "Αναζήτηση Ταινιών",
      desc: "Πληκτρολογήστε μια ταινία για να δείτε λεπτομέρειες από τη βάση δεδομένων OMDb.",
      placeholder: "Πληκτρολογήστε τίτλο ταινίας...",
      searchBtn: "Αναζήτηση",
      historyBtn: "Εμφάνιση ιστορικού",
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

    const loginLink = document.getElementById("nav-login");
    if (loginLink) loginLink.textContent = t.login;
    const logoutLink = document.getElementById("nav-logout");
    if (logoutLink) logoutLink.textContent = t.logout;

    document.querySelector(".page-header h1").textContent = t.title;
    document.querySelector(".page-header p").textContent = t.desc;
    document.getElementById("searchInput").placeholder = t.placeholder;
    document.getElementById("searchBtn").textContent = t.searchBtn;

    const historyBtn = document.getElementById("historyBtn");
    if (historyBtn) historyBtn.textContent = t.historyBtn;

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