// document.addEventListener("DOMContentLoaded", async () => {
//     try {
//         const response = await fetch("http://localhost:8080/auth/check", {
//             credentials: "include"
//         });
//
//         if (!response.ok) {
//             window.location.href = "login.html"; // Перенаправляем на страницу входа
//             return;
//         }
//
//         const user = await response.json();
//         document.title = `Чат - ${user.username}`; // Меняем заголовок на имя юзера
//     } catch (error) {
//         console.error("Ошибка проверки авторизации", error);
//         window.location.href = "login.html"; // Если ошибка — тоже отправляем на логин
//     }
// });

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
document.addEventListener("DOMContentLoaded", async () => {
    const roomsContainer = document.getElementById("roomList");

    try {
        const response = await fetch("http://localhost:8080/rooms/getAll");
        const rooms = await response.json();

        roomsContainer.innerHTML = "";

        rooms.forEach(room => {
            const roomElement = document.createElement("li");
            roomElement.textContent = room.name;
            roomElement.addEventListener("click", () => {
                window.location.href = `chat.html?room=${room.name}`;
            });
            roomsContainer.appendChild(roomElement);
        });
    } catch (error) {
        console.error("Ошибка загрузки комнат", error);
    }
});

document.getElementById("createRoomBtn").addEventListener("click", () => {
    window.location.href = "create-room.html";
});
