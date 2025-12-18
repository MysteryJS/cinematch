async function loadLeaderboard() {
    const response = await fetch('/api/user/leaderboard');
    if (!response.ok) {
        document.getElementById('leaderboard').innerHTML =
            "<div class='lb-error'>Leaderboard is not available.</div>";
        return;
    }
    const data = await response.json();
    let html = `<h3 class="lb-title">Leaderboard</h3>
        <table class="lb-table">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Best score</th>
                    <th>Games</th>
                </tr>
            </thead>
            <tbody>`;
    data.forEach((entry, idx) => {
        html += `<tr>
            <td>${idx + 1}</td>
            <td>${escapeHtml(entry.username)}</td>
            <td>${entry.score}</td>
            <td>${entry.gamesPlayed}</td>
        </tr>`;
    });
    html += `</tbody></table>`;
    document.getElementById('leaderboard').innerHTML = html;
}

window.addEventListener("DOMContentLoaded", () => {
    loadLeaderboard();
});

function postScore(finalScore) {
    fetch('/api/user/quiz-history', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ score: finalScore })
    }).then(res => {
        if (res.ok) loadLeaderboard();
    }).catch(() => { });
}

let quizQuestions = [];
let currentIndex = 0;
let score = 0;
let answersLog = [];

const progressText = document.getElementById('progressText');
const scoreText = document.getElementById('scoreText');
const questionTitle = document.getElementById('questionTitle');
const choicesEl = document.getElementById('choices');
const nextBtn = document.getElementById('nextBtn');
const restartBtn = document.getElementById('restartBtn');
const resultSummary = document.getElementById('resultSummary');

async function getFavoritesForCurrentUser() {
    const res = await fetch('/api/quiz');
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const data = await res.json();
    return data;
}
async function fetchQuizForUserFavorites() {
    const favoriteTitles = await getFavoritesForCurrentUser();
    const contextText = favoriteTitles.join(', ');
    const res = await fetch("https://mysterygre-quiz.hf.space/quiz", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer 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"
        },
        body: JSON.stringify({ context: contextText })
    });
    if (!res.ok) throw new Error('Quiz fetch error ' + res.status);
    const data = await res.json();
    return (data.quiz || []).map(q => ({
        question: q.question,
        answers: q.options,
        correctIndex: q.options.findIndex(opt => opt === q.answer)
    }));
}
async function startQuiz() {

    quizQuestions = [];
    currentIndex = 0;
    score = 0;
    answersLog = [];

    progressText.textContent = 'Loading...';
    scoreText.textContent = 'Score: 0';
    questionTitle.textContent = 'Loading question...';
    choicesEl.innerHTML = '';
    resultSummary.hidden = true;
    restartBtn.hidden = true;
    nextBtn.hidden = true;

    try {

        quizQuestions = await fetchQuizForUserFavorites();

        if (quizQuestions.length === 0) {
            questionTitle.textContent = 'Error from API.';
            return;
        }

        nextBtn.hidden = false;
        renderQuestion();
    } catch (err) {
        console.error(err);
        questionTitle.textContent =
            'Error from API.';
    }
}

function renderQuestion() {
    const item = quizQuestions[currentIndex];

    progressText.textContent = `question ${currentIndex + 1} / ${quizQuestions.length}`;
    scoreText.textContent = `Score: ${score}`;

    questionTitle.textContent = decodeHtml(item.question);

    choicesEl.innerHTML = '';
    choicesEl.dataset.answered = 'false';

    item.answers.forEach((ans, i) => {
        const btn = document.createElement('button');
        btn.className = 'choice-btn';
        btn.type = 'button';
        btn.setAttribute('data-index', i);
        btn.setAttribute('role', 'option');
        btn.textContent = decodeHtml(ans);
        btn.addEventListener('click', onChoiceClick);
        btn.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                btn.click();
            }
        });
        choicesEl.appendChild(btn);
    });

    nextBtn.disabled = true;
    nextBtn.setAttribute('aria-disabled', 'true');
    nextBtn.hidden = false;
    restartBtn.hidden = true;
    resultSummary.hidden = true;
}

function onChoiceClick(e) {
    const selected = Number(e.currentTarget.getAttribute('data-index'));
    const item = quizQuestions[currentIndex];
    if (choicesEl.dataset.answered === 'true') return;
    choicesEl.dataset.answered = 'true';

    const buttons = Array.from(choicesEl.querySelectorAll('.choice-btn'));
    buttons.forEach((b, idx) => {
        b.disabled = true;
        b.classList.remove('chosen-correct', 'chosen-wrong', 'correct');
        if (idx === item.correctIndex) {
            b.classList.add('correct');
        }
    });

    const selectedBtn = e.currentTarget;
    let correctNow = false;
    if (selected === item.correctIndex) {
        selectedBtn.classList.add('chosen-correct');
        score++;
        correctNow = true;
        scoreText.textContent = `Score: ${score}`;
    } else {
        selectedBtn.classList.add('chosen-wrong');
    }

    answersLog.push({
        question: item.question,
        selectedIndex: selected,
        correctIndex: item.correctIndex,
        answers: item.answers.slice()
    });

    const feedback = document.createElement('div');
    feedback.className = 'feedback';
    feedback.textContent = correctNow ? 'Σωστό!' : 'Λάθος!';
    choicesEl.appendChild(feedback);

    nextBtn.disabled = false;
    nextBtn.setAttribute('aria-disabled', 'false');
}

nextBtn.addEventListener('click', () => {
    currentIndex++;
    if (currentIndex < quizQuestions.length) {
        renderQuestion();
        window.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
        showSummary();
    }
});

restartBtn.addEventListener('click', () => startQuiz());

function showSummary() {
    progressText.textContent = 'Finish';
    questionTitle.textContent = `Final score: ${score} / ${quizQuestions.length}`;
    choicesEl.innerHTML = '';
    nextBtn.hidden = true;
    restartBtn.hidden = false;
    resultSummary.hidden = false;

    let html = `<h3>Results</h3>`;
    html += `<p>Total correct answers: <strong>${score}</strong> από ${quizQuestions.length}</p>`;
    html += `<div class="review">`;

    answersLog.forEach((a, i) => {
        const correctTextRaw = a.answers[a.correctIndex] ?? '';
        const selectedTextRaw = a.answers[a.selectedIndex] ?? '';
        const correctText = decodeHtml(correctTextRaw);
        const selectedText = decodeHtml(selectedTextRaw);
        const ok = a.selectedIndex === a.correctIndex;

        html += `<div class="review-item">
                        <div class="r-q"><strong>Q${i + 1}:</strong> ${escapeHtml(decodeHtml(a.question))}</div>
                        <div class="r-ans ${ok ? 'ok' : 'bad'}">
                            <div>Η δική σου απάντηση: <span class="user">${escapeHtml(selectedText)}</span></div>
                            <div>Corre answer: <span class="correct">${escapeHtml(correctText)}</span></div>
                        </div>
                     </div>`;
    });

    html += `</div>`;
    resultSummary.innerHTML = html;
    postScore(score);
    window.scrollTo({ top: 0, behavior: 'smooth' });
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

function decodeHtml(html) {
    const txt = document.createElement('textarea');
    txt.innerHTML = html;
    return txt.value;
}

startQuiz();

(function updateFooterYear() {
    const y = new Date().getFullYear();
    const el = document.getElementById('year');
    if (el) el.textContent = y;
})();
