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