'use strict';

document.querySelector('#welcomeForm').addEventListener('submit', connectTest, true)
document.querySelector('#dialogueForm').addEventListener('submit', sendMessageTest, true)

let name = null;
let stompClient = null;

function connectTest(event) {
    name = document.querySelector('#name').value.trim();
    if (name) {
        document.querySelector('#welcome-page').classList.add('hidden');
        document.querySelector('#dialogue-page').classList.remove('hidden');
        let socket = new SockJS('/websocketApp');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, connectionSuccessTest);
    }
    event.preventDefault();
}

function connectionSuccessTest() {
    stompClient.subscribe('/topic/javainuse', onMessageReceivedTest);
    stompClient.send("/app/chat.newUser", {}, JSON.stringify({
        sender: name,
        type: 'newUser'
    }))
}

function sendMessageTest(event) {
    let messageContent = document.querySelector('#chatMessage').value.trim();
    if (messageContent && stompClient) {
        let chatMessage = {
            sender: name,
            content: document.querySelector('#chatMessage').value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON
            .stringify(chatMessage));
        document.querySelector('#chatMessage').value = '';
    }
    event.preventDefault();
}

function onMessageReceivedTest(payload) {
    let message = JSON.parse(payload.body);
    let messageElement = document.createElement('li');
    if (message.type === 'newUser') {
        messageElement.classList.add('event-data');
        message.content = message.sender + 'has joined the chat';
    } else if (message.type === 'Leave') {
        messageElement.classList.add('event-data');
        message.content = message.sender + 'has left the chat';
    } else {
        messageElement.classList.add('message-data');
        let element = document.createElement('i');
        let text = document.createTextNode(message.sender[0]);
        element.appendChild(text);
        messageElement.appendChild(element);
        let usernameElement = document.createElement('span');
        let usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }
    let textElement = document.createElement('p');
    let messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    document.querySelector('#messageList').appendChild(messageElement);
    document.querySelector('#messageList').scrollTop = document
        .querySelector('#messageList').scrollHeight;
}

function clearVacancies() {
    confirm("Вы уверены, что хотите очистить список вакансий?")
}

function checkAmount() {
    let amount = document.getElementById("amount").value
    if (amount < 35 || amount > 1000) {
        alert("Количество вакансий не должно быть меньше 35 и больше 1000!")
    }
}