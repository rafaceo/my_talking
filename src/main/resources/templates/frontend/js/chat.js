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
        console.log(`[LOG] WebSocket подключен к комнате: ${roomName}`);
        try {
            const response = await fetch(`http://localhost:8080/api/chat/${roomName}/messages`);
            const messages = await response.json();
            console.log(`[LOG] Загружена история сообщений (${messages.length} шт.)`);
            messages.forEach(msg => displayMessage(msg.username, msg.text, msg.timestamp));
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
