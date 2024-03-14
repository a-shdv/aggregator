'use strict';
document.querySelector('#vacancyForm').addEventListener('submit', connect, true)
// document.querySelector('#progressbar').style.display = 'hidden'

let stompClient = null;
let vacancy = document.querySelector('#vacancy')
let progressBar = document.querySelector('#progressbar')
let progressBarLoader = document.querySelector('#progressbar-loader')
let spaceBefore = document.querySelector('#spaceBefore')
let spaceAfter = document.querySelector('#spaceAfter')
let buttonCancelSearch = document.querySelector('#buttonCancelSearch')
let buttonOk = document.querySelector('#buttonOk')

function connect() {
    event.preventDefault()
    // document.querySelector('#progressbar').style.display = 'block'
    let socket = new SockJS('/aggregator');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame)

        // ON RECEIVE
        stompClient.subscribe('/topic/progressbar', function (response) {
            let message = JSON.parse(response.body);
            progressBarLoader.style.width = message + '%'
            if (parseInt(progressBarLoader.style.width) >= parseInt('100%')) {
                progressBarLoader.classList.remove('bg-warning')
                progressBarLoader.classList.add('bg-success')
                buttonCancelSearch.style.display = 'none'
                buttonOk.style.display = ''
                // progressBarLoader.style.width = '100%';
                // progressBarLoader.setAttribute('aria-valuenow', '25');
                // progressBarLoader.setAttribute('aria-valuemin', '0');
                // progressBarLoader.setAttribute('aria-valuemax', '100');
            }
        });

        // BEFORE SEND
        vacancy.style.display = 'none'
        spaceBefore.style.height = '50vh'
        spaceAfter.style.height = '50vh'
        progressBar.style.display = ''
        buttonCancelSearch.style.display = ''
        // document.querySelector('#emptyVacanciesAlert').style.display = ''
        // document.querySelector('#vacanciesList').style.display = ''
        // document.querySelector('#clearButton').style.display = ''

        stompClient.send("/endpoint/toJava", {}, JSON.stringify({
            username: document.querySelector("#username").value,
            title: document.querySelector("#title").value,
            salary: document.querySelector('#salary').value,
            onlyWithSalary: document.querySelector('#onlyWithSalary').checked,
            experience: parseInt(document.querySelector('input[name="experience"]:checked').value),
            cityId: parseInt(document.querySelector('#cityId').value),
            isRemoteAvailable: document.querySelector('#isRemoteAvailable').checked
        }));
    }, function (error) {
        console.error('Error during WebSocket connection: ' + error);
        // document.querySelector('#progressbar').style.display = 'none';
    });
}

function clearVacancies() {
    confirm("Вы уверены, что хотите очистить список вакансий?")
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