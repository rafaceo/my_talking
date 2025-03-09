document.getElementById("registerForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const response = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ firstName, lastName, email, password })
    });

    const data = await response.json();
    document.getElementById("message").textContent = data.message || "Успешная регистрация!";

    if (response.ok) {
        setTimeout(() => {
            window.location.href = "login.html";  // Переход на страницу логина через 2 сек
        }, 2000);
    }
});
