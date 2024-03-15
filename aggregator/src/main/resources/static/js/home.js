'use strict'

const vacancy = document.querySelector('#vacancy')
const vacancyForm = document.querySelector('#vacancyForm')
const searchVacanciesButton = document.querySelector('#searchVacanciesButton')

const counter = document.querySelector('#counter')
const progressbar = document.querySelector('#progressbar')
const progressbarLoader = document.querySelector('#progressbar-loader')
const cancelSearchForm = document.querySelector('#cancelSearchForm')
const okForm = document.querySelector('#okForm')

let stompClient = null;
let username = null;
let isFirstStart = true;

function connect(event) {
    username = document.querySelector('#username').value;

    if (username) {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived); // TODO

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

    localStorage.setItem('username', username);
}

function onError(error) {
    // connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    // connectingElement.style.color = 'red';
    console.log('Error connecting to websocket server: ' + error)
}

function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);

    switch (message.type) {
        case 'JOIN':
            console.log('joined')
            break
        case 'LEAVE':
            console.log('left')
            break
        case 'RECEIVE':
            let messageCounter = message.counter + '%'
            counter.textContent = messageCounter
            progressbarLoader.style.width = messageCounter
            if (progressbarLoader.style.width >= '80%') {
                counter.classList.remove('text-warning')
                counter.classList.add('text-success')

                progressbarLoader.classList.remove('bg-warning')
                progressbarLoader.classList.add('bg-success')

                cancelSearchForm.style.display = 'none'
                okForm.style.display = ''
            }
            break
    }
    // let messageElement = document.createElement('li');
    //
    // if (message.type === 'JOIN') {
    //     messageElement.classList.add('event-message');
    //     message.content = message.sender + ' joined!';
    // } else if (message.type === 'LEAVE') {
    //     messageElement.classList.add('event-message');
    //     message.content = message.sender + ' left!';
    // } else {
    //     messageElement.classList.add('chat-message');
    //
    //     let avatarElement = document.createElement('i');
    //     let avatarText = document.createTextNode(message.sender[0]);
    //     avatarElement.appendChild(avatarText);
    //
    //     messageElement.appendChild(avatarElement);
    //
    //     let usernameElement = document.createElement('span');
    //     let usernameText = document.createTextNode(message.sender);
    //     usernameElement.appendChild(usernameText);
    //     messageElement.appendChild(usernameElement);
    // }
    //
    // let textElement = document.createElement('p');
    // let messageText = document.createTextNode(message.content);
    // textElement.appendChild(messageText);
    //
    // messageElement.appendChild(textElement);
    //
    // messageArea.appendChild(messageElement);
    // messageArea.scrollTop = messageArea.scrollHeight;
}

function sendMessage(event) {
    const title = document.querySelector("#title").value
    const salary = document.querySelector('#salary').value
    const onlyWithSalary = document.querySelector('#onlyWithSalary').checked
    const experience = parseInt(document.querySelector('input[name="experience"]:checked').value)
    const cityId = parseInt(document.querySelector('#cityId').value)
    const isRemoteAvailable = document.querySelector('#isRemoteAvailable').checked
    const type = 'CHAT'
    const messageContent = {username, title, salary, onlyWithSalary, experience, cityId, isRemoteAvailable, type}
    if (messageContent && stompClient) {
        // hide vacancy form and show progressbar, counter
        vacancy.style.display = 'none'
        progressbar.style.display = ''
        counter.style.display = ''
        cancelSearchForm.style.display = ''

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(messageContent));
    }
    event.preventDefault();
}

window.onload = (event) => connect(event)
searchVacanciesButton.addEventListener('click', sendMessage)