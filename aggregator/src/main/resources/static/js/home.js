'use strict'

const vacancy = document.querySelector('#vacancy')
const vacancyForm = document.querySelector('#vacancyForm')
const searchVacanciesButton = document.querySelector('#searchVacanciesButton')

const counter = document.querySelector('#counter')
const progressbar = document.querySelector('#progressbar')
const progressbarLoader = document.querySelector('#progressbar-loader')
const okButton = document.querySelector('#okButton')

let stompClient = null;
let username = null;

function connect(event) {
    event.preventDefault();

    username = document.querySelector('#username').value;
    if (username) {
        const socket = new SockJS('/ws');

        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )
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
            sendMessage()
            break
        case 'LEAVE':
            console.log('left')
            break
        case 'RECEIVE':
            let messageCounter = message.counter + '%'
            counter.textContent = messageCounter
            progressbarLoader.style.width = messageCounter

            if (parseInt(progressbarLoader.style.width) >= 12 && parseInt(progressbarLoader.style.width) <= 80) {
                counter.classList.remove('text-warning')
                counter.classList.add('text-success')

                progressbarLoader.classList.remove('bg-warning')
                progressbarLoader.classList.add('bg-success')

                okButton.style.display = ''
            } else if (parseInt(progressbarLoader.style.width) > 80) {
                counter.textContent = '100%'
                progressbarLoader.style.width = '100%'
                stompClient.disconnect()
            }
            break
    }
}

function sendMessage() {
    window.onbeforeunload = () => "You have attempted to leave this page.  If you have made any changes to the fields without clicking the Save button, your changes will be lost.  Are you sure you want to exit this page?";

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

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(messageContent));
    }
}

// window.onload = (event) => {connect(event)}
searchVacanciesButton.addEventListener('click', connect)