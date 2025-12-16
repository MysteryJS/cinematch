const regUsernameInput = document.getElementById('regName');
regUsernameInput.addEventListener('input', function (e) {
    let clean = this.value.toLowerCase().replace(/[^a-z_]/g, '');
    if (this.value !== clean) {
        this.value = clean;
    }
});

document.getElementById('registerForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const name = regUsernameInput.value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value;

    let response1 = await fetch(`/api/user/email-exists?email=${encodeURIComponent(email)}`);
    if (response1.ok) {
        const exists = await response1.json();
        if (exists) {
            document.getElementById('feedback').className = 'msg error';
            document.getElementById('feedback').textContent = 'Το email υπάρχει ήδη!';
            document.getElementById('feedback').style.display = 'block';
            return;
        }
    }

    if (!/^[a-z_]+$/.test(name)) {
        document.getElementById('feedback').className = 'msg error';
        document.getElementById('feedback').textContent = 'Το username πρέπει να έχει μόνο μικρά γράμματα ή "_"';
        document.getElementById('feedback').style.display = 'block';
        return;
    }

    const payload = {
        username: name,
        email: email,
        passwordHash: password
    };

    let response = await fetch('/api/user/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    if (response.ok) {
        const user = await response.json();
        document.getElementById('feedback').className = 'msg success';
        document.getElementById('feedback').textContent = 'Εγγραφή επιτυχής! Μπορείς να κάνεις σύνδεση.';
        document.getElementById('feedback').style.display = 'block';
        document.getElementById('registerForm').reset();
    } else {
        const error = await response.text();
        document.getElementById('feedback').className = 'msg error';
        document.getElementById('feedback').textContent = 'Σφάλμα εγγραφής: ' + error;
        document.getElementById('feedback').style.display = 'block';
    }
});