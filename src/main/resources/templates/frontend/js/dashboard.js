document.addEventListener("DOMContentLoaded", async () => {
    try {
        console.log("Отправляем токен:", localStorage.getItem("accessToken"));
        const response = await fetch("http://localhost:8080/auth/check", {
            method: "GET",
            credentials: "include",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
            }
        });

        if (!response.ok) {
            throw new Error("Неавторизованный доступ");
        }

        const user = await response.text();
        console.log("Ответ сервера:", user);
        document.title = `Чат - ${user.username}`;
    } catch (error) {
            console.error("Ошибка проверки авторизации", error);
            alert("Ошибка авторизации: " + error.message); // Покажет ошибку перед редиректом
            window.location.href = "login.html"
    }
});


document.addEventListener("DOMContentLoaded", async () => {
    const roomsContainer = document.getElementById("roomsContainer");
    const noRoomsMessage = document.getElementById("noRoomsMessage");
    const roomTitle = document.getElementById("roomTitle");
    const roomMessage = document.getElementById("roomMessage");

    try {
        const response = await fetch("http://localhost:8080/rooms/getAll");
        const rooms = await response.json();

        if (rooms.length === 0) {
            noRoomsMessage.style.display = "block";
        } else {
            noRoomsMessage.style.display = "none";
            rooms.forEach(room => {
                const roomElement = document.createElement("div");
                roomElement.classList.add("room");
                roomElement.innerHTML = `<h3>${room.name}</h3>`;

                roomElement.addEventListener("click", () => {
                    document.querySelectorAll(".room").forEach(el => el.classList.remove("active"));
                    roomElement.classList.add("active");

                    roomTitle.textContent = `Комната: ${room.name}`;
                    roomMessage.innerHTML = `<button onclick="joinRoom('${room.name}')">Войти</button>`;
                });

                roomsContainer.appendChild(roomElement);
            });
        }
    } catch (error) {
        console.error("Ошибка загрузки комнат", error);
    }
});

async function joinRoom(name) {
    const password = prompt("Введите пароль комнаты:");
    if (password) {
        const response = await fetch(`http://localhost:8080/rooms/join?name=${name}&password=${password}`, {
            method: "POST"
        });

        const result = await response.text();
        if (response.ok) {
            alert(result);
            window.location.href = `chat.html?room=${name}`;
        } else {
            alert("Ошибка: " + result);
        }
    }
}


document.getElementById("createRoomBtn").addEventListener("click", () => {
    window.location.href = "create-room.html";
});
