'use strict'

const vacancy = document.querySelector('#vacancy')
const vacancyForm = document.querySelector('#vacancyForm')
const searchVacanciesButton = document.querySelector('#searchVacanciesButton')

const counter = document.querySelector('#counter')
const progressbar = document.querySelector('#progressbar')
const progressbarLoader = document.querySelector('#progressbar-loader')
const cancelSearchForm = document.querySelector('#cancelSearchForm')
const okButton = document.querySelector('#okButton')

let stompClient = null;
let username = null;

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

            if (parseInt(progressbarLoader.style.width) >= 36 && parseInt(progressbarLoader.style.width) <= 80) {
                counter.classList.remove('text-warning')
                counter.classList.add('text-success')

                progressbarLoader.classList.remove('bg-warning')
                progressbarLoader.classList.add('bg-success')

                cancelSearchForm.style.display = 'none'
                okButton.style.display = ''
            } else if (parseInt(progressbarLoader.style.width) > 80) {
                counter.textContent = '100%'
                progressbarLoader.style.width = '100%'
                // stompClient.disconnect(() => {
                //     alert("See you next time!")
                // });
            }
            break
    }
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

function cancelSearch() {
    vacancy.style.display = ''
    progressbar.style.display = 'none'
    counter.style.display = 'none'
    cancelSearchForm.style.display = 'none'
    okButton.style.display = 'none'
    stompClient.disconnect()
}

window.onload = (event) => {connect(event)}
searchVacanciesButton.addEventListener('click', sendMessage)
cancelSearchForm.addEventListener('click', cancelSearch)