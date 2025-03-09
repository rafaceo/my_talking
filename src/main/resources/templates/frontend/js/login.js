document.getElementById("loginForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });

    let data;
    try {
        data = await response.json();
    } catch (error) {
        console.error("Ошибка парсинга JSON:", error);
        data = {};
    }

    if (!response.ok) {
        document.getElementById("message").textContent = data.message || "Неверный email или пароль!";
        document.getElementById("message").style.color = "red";
        return;
    }

    document.getElementById("message").textContent = "Успешный вход!";
    document.getElementById("message").style.color = "green";

    const usernameResponse = await fetch("http://localhost:8080/user/getUsernameByEmail", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email })
    });

    if (!usernameResponse.ok) {
        document.getElementById("message").textContent = "Ошибка получения имени пользователя!";
        document.getElementById("message").style.color = "red";
        return;
    }

    const username = await usernameResponse.text();
    console.log("Полученный username:", username);
    localStorage.setItem("username", username);
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);

    window.location.href = "dashboard.html";
});
