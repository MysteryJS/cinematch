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

function renderResults(data) {

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

    if (data.closest_celebrity && data.score !== undefined) {
        results.innerHTML = `
            <div class="result-card">
                <h3>Result</h3>
                <p><strong>Actor:</strong> ${escapeHtml(data.closest_celebrity)}</p>
                <p><strong>Similarity:</strong> ${Math.round(similarity)}%</p>
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
            headers: {
                "Authorization": "Bearer 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"
            },
            body: fd
        });
        const json = await res.json();
        renderResults(json);
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
