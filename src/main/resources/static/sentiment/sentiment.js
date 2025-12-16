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
        resultDiv.innerHTML = "<span class='error'>Συμπλήρωσε κείμενο.</span>";
        return;
    }

    resultDiv.innerHTML = "Γίνεται αποστολή στο API...";

    try {
        const response = await fetch("https://mysterygre-cinematch-sentiment-analysis.hf.space/recommend", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer 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"
            },
            body: JSON.stringify({ text })
        });

        if (!response.ok) {
            resultDiv.innerHTML = "<span class='error'>Σφάλμα API / δεν είναι διαθέσιμο.</span>";
            return;
        }

        let data;
        try {
            data = await response.json();
        } catch (parseErr) {
            resultDiv.innerHTML = "<span class='error'>Το API επέστρεψε μη αναγνωρίσιμο JSON.</span>";
            return;
        }

        resultDiv.innerHTML = "";

        if (data.genres && Array.isArray(data.genres)) {
            genresDiv.innerHTML =
                "<strong>Είδη (Genres):</strong> <ul>" +
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
                `<strong>Πιθανότητες συναισθημάτων:</strong>
                <table class="emotions-table">
                  <thead><tr><th>Συναίσθημα</th><th>Πιθανότητα</th></tr></thead>
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
            titlesDiv.innerHTML = "<strong>Αναλυτικά αποτελέσματα ταινιών:</strong>";
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
                    // poster handling
                    let poster = details.Poster && details.Poster !== 'N/A' ? details.Poster : '/placeholder-poster.png';

                    li.innerHTML = `
        <div class="movie-card">
          <img class="movie-poster" src="${escapeHtml(poster)}" alt="${escapeHtml(details.Title)} poster"
               onerror="this.onerror=null;this.src='/placeholder-poster.png'">
          <div class="movie-meta">
            <h3 class="movie-title">${escapeHtml(details.Title || title)} ${details.Year ? '(' + escapeHtml(details.Year) + ')' : ''}</h3>
            <p><strong>Σκηνοθέτης:</strong> ${escapeHtml(details.Director || '—')}</p>
            <p><strong>Είδος:</strong> ${escapeHtml(details.Genre || '—')}</p>
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
            titlesDiv.innerHTML = "<strong>Ταινίες:</strong> —";
        }

    } catch (err) {
        resultDiv.innerHTML = "<span class='error'>Σφάλμα επικοινωνίας με το API!</span>";
        console.error(err);
    }
};

(function updateFooterYear() {
    const y = new Date().getFullYear();
    const el = document.getElementById('year');
    if (el) el.textContent = y;
})();