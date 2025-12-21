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

document.addEventListener("DOMContentLoaded", function () {
    const langBtn = document.getElementById("lang-btn");
    const langFlag = document.getElementById("lang-flag");
    const langText = document.getElementById("lang-text");

    const translations = {
        en: {
            mainTitle: "Sign up / Login",
            signupTitle: "Sign up",
            loginTitle: "Login",
            username: "Username",
            email: "Email",
            password: "Password",
            passwordHint: "Password (at least 6 characters)",
            regHint: "Only lowercase and \"_\", no spaces or uppercase",
            signupBtn: "sign up",
            loginBtn: "Login",
            rights: "All rights reserved.",
            designed: "Designed with ❤ for movie lovers.",
            tou: "Terms of Use",
            flag: "https://flagcdn.com/w40/gr.png", btnText: "GR"
        },
        el: {
            mainTitle: "Εγγραφή / Σύνδεση",
            signupTitle: "Εγγραφή",
            loginTitle: "Σύνδεση",
            username: "Όνομα Χρήστη",
            email: "Email",
            password: "Κωδικός",
            passwordHint: "Κωδικός (τουλάχιστον 6 χαρακτήρες)",
            regHint: "Μόνο πεζά και \"_\", χωρίς κενά ή κεφαλαία",
            signupBtn: "εγγραφή",
            loginBtn: "Σύνδεση",
            rights: "Με επιφύλαξη παντός δικαιώματος.",
            designed: "Σχεδιάστηκε με ❤ για τους λάτρεις του σινεμά.",
            tou: "Όροι Χρήσης",
            flag: "https://flagcdn.com/w40/gb.png", btnText: "EN"
        }
    };

    let currentLang = "en";

    langBtn.addEventListener("click", () => {
        currentLang = currentLang === "en" ? "el" : "en";
        const t = translations[currentLang];

        document.querySelector("h1").textContent = t.mainTitle;
        document.querySelectorAll("h2")[0].textContent = t.signupTitle;
        document.querySelectorAll("h2")[1].textContent = t.loginTitle;

        const regLabels = document.querySelectorAll("#registerForm label");
        regLabels[0].textContent = t.username;
        regLabels[1].textContent = t.email;
        regLabels[2].textContent = t.passwordHint;
        document.querySelector("#registerForm small").textContent = t.regHint;
        document.querySelector("#registerForm button").textContent = t.signupBtn;

        const loginLabels = document.querySelectorAll("form[action='/login'] label");
        loginLabels[0].textContent = t.username;
        loginLabels[1].textContent = t.password;
        document.querySelector("form[action='/login'] button").textContent = t.loginBtn;

        document.querySelector(".footer-bottom .small").childNodes[2].textContent = " CineMatch — " + t.rights;
        document.querySelectorAll(".footer-bottom .small")[1].textContent = t.designed;
        document.querySelector(".footer-link").textContent = t.tou;

        langFlag.src = t.flag;
        langText.textContent = t.btnText;
    });
});