let socket;
const messagesContainer = document.getElementById("messages");
const messageInput = document.getElementById("messageInput");
const sendButton = document.getElementById("sendButton");

document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    const roomName = urlParams.get("room");
    if (!roomName) {
        alert("Комната не найдена!");
        window.location.href = "dashboard.html";
        return;
    }

    document.getElementById("roomTitle").textContent = `Чат: ${roomName}`;

    socket = new WebSocket(`ws://localhost:8080/chat/${roomName}`);

    socket.onopen = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/chat/${roomName}/messages`);
            if (!response.ok) {
                throw new Error(`Ошибка запроса: ${response.status} - ${response.statusText}`);
            }

            const messages = await response.json();
            console.log("[LOG] Загружена история сообщений:", messages);

            // Проверяем, является ли messages массивом
            if (Array.isArray(messages)) {
                messages.forEach(msg => displayMessage(msg.username, msg.text, msg.timestamp));
            } else {
                console.error("[ERROR] Неверный формат данных истории сообщений:", messages);
            }
        } catch (error) {
            console.error("[ERROR] Ошибка загрузки истории сообщений:", error);
        }
    };


    socket.onmessage = (event) => {
        console.log("[LOG] Получено сообщение:", event.data);
        const message = JSON.parse(event.data);
        displayMessage(message.username, message.text, message.timestamp);
    };

    socket.onerror = (error) => console.error("[ERROR] WebSocket ошибка:", error);

    socket.onclose = () => console.log("[LOG] WebSocket соединение закрыто");

    sendButton.addEventListener("click", sendMessage);
    messageInput.addEventListener("keypress", (e) => {
        if (e.key === "Enter") sendMessage();
    });
});

function sendMessage() {
    const text = messageInput.value.trim();
    if (!text) return;

    const message = {
        username: localStorage.getItem("username") || "Аноним",
        text,
        timestamp: new Date().toISOString()
    };

    console.log("[LOG] Отправка сообщения:", message);
    socket.send(JSON.stringify(message));
    displayMessage(message.username, message.text, message.timestamp, true);
    messageInput.value = "";
}

function displayMessage(username, text, timestamp, isOwn = false) {
    const messageElement = document.createElement("div");
    messageElement.classList.add("message", isOwn ? "own-message" : "other-message");
    messageElement.innerHTML = `<strong>${username}</strong> <span>${new Date(timestamp).toLocaleTimeString()}</span><p>${text}</p>`;
    messagesContainer.appendChild(messageElement);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

document.addEventListener("DOMContentLoaded", async () => {
    const roomList = document.getElementById("roomList");
    if (!roomList) {
        console.error("[ERROR] Элемент roomList не найден!");
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/rooms/getAll");
        if (!response.ok) throw new Error(`Ошибка запроса: ${response.status}`);

        const rooms = await response.json();
        console.log("[LOG] Получены комнаты:", rooms);

        roomList.innerHTML = ""; // Очищаем список перед добавлением
        if (rooms.length === 0) {
            roomList.innerHTML = "<li>Нет доступных комнат</li>";
        } else {
            rooms.forEach(room => {
                const roomElement = document.createElement("li");
                roomElement.textContent = room.name;
                roomElement.addEventListener("click", () => {
                    window.location.href = `chat.html?room=${room.name}`;
                });
                roomList.appendChild(roomElement);
            });
        }
    } catch (error) {
        console.error("[ERROR] Ошибка загрузки комнат:", error);
    }
});

document.addEventListener("DOMContentLoaded", async () => {
    const roomList = document.getElementById("roomList");
    if (!roomList) {
        console.error("[ERROR] Элемент roomList не найден!");
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/rooms/getAll");
        if (!response.ok) throw new Error(`Ошибка запроса: ${response.status}`);

        const rooms = await response.json();
        console.log("[LOG] Получены комнаты:", rooms);

        roomList.innerHTML = ""; // Очищаем список перед добавлением
        if (rooms.length === 0) {
            roomList.innerHTML = "<li>Нет доступных комнат</li>";
        } else {
            rooms.forEach(room => {
                const roomElement = document.createElement("li");
                roomElement.textContent = room.name;
                roomElement.addEventListener("click", () => {
                    validateRoomPassword(room.name);
                });
                roomList.appendChild(roomElement);
            });
        }
    } catch (error) {
        console.error("[ERROR] Ошибка загрузки комнат:", error);
    }
});

async function validateRoomPassword(roomName) {
    const password = prompt(`Введите пароль для комнаты "${roomName}":`);
    if (!password) {
        alert("Пароль не введён!");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/rooms/join?name=${roomName}&password=${password}`, {
            method: "POST",
        });

        const result = await response.text();

        if (response.ok) {
            alert("Доступ разрешён!");
            window.location.href = `chat.html?room=${roomName}`;
        } else {
            alert("Ошибка: " + result);
        }
    } catch (error) {
        console.error("[ERROR] Ошибка входа в комнату:", error);
        alert("Ошибка подключения к серверу.");
    }
}


