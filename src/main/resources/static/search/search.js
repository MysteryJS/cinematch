const searchBtn = document.getElementById("searchBtn");
    const input = document.getElementById("searchInput");
    const results = document.getElementById("movieResults");

    searchBtn.addEventListener("click", () => {
      const title = input.value.trim();
      if (!title) {
        results.innerHTML = "<p class='error'>Γράψε μια ταινία πρώτα.</p>";
        return;
      }

      document.getElementById('movieResults').innerHTML = '';
      document.getElementById('kpiResults').innerHTML = '';

      fetch(`http://localhost:8080/api/movie/search?title=${encodeURIComponent(title)}`)
        .then(res => res.json())
        .then(data => {
          console.log(data);

          if (data.Response === "False") {
            results.innerHTML = "<p class='error'>Δεν βρέθηκε ταινία.</p>";
            return;
          }
          results.innerHTML = `
                <div class="movie-card">
                    <img src="${data.Poster}" alt="Poster">
                    <div class="movie-info">
                        <h2>${data.Title} (${data.Year})</h2>
                        <p><strong>Ηθοποιοί:</strong> ${data.Actors}</p>
                        <p><strong>Σκηνοθέτης:</strong> ${data.Director}</p>
                        <p><strong>Είδος:</strong> ${data.Genre}</p>
                        <p><strong>IMDB Rating:</strong> 🌟 ${data.imdbRating}</p>
                        <p><strong>Πλοκή:</strong> ${data.Plot}</p>
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
          results.innerHTML = "<p class='error'>Σφάλμα σύνδεσης.</p>";
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
            historyContainer.innerHTML = "<p>Δεν υπάρχει ιστορικό.</p>";
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
        }).catch(() => alert("Πρέπει να είσαι συνδεδεμένος"));
    });