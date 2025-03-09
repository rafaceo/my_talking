document.getElementById("loginForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });

    const data = await response.json();
    document.getElementById("message").textContent = data.message || "Успешный вход!";

    if (response.ok) {
        window.location.href = "dashboard.html";  // После успешного входа переходим в личный кабинет
    } else {
        document.getElementById("message").textContent = data.message || "Неверный пароль"
    }
});
