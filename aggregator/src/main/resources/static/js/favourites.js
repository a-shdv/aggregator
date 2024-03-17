const spinner = document.querySelector('#spinner')
const pdfEmailButton = document.querySelector('#pdfEmailButton')
const error = document.querySelector('#error')
const success = document.querySelector('#success')
const clearFavouritesForm = document.querySelector('#clearFavouritesForm')

const favouritesTable = document.querySelector('#favouritesTable')
const pagination = document.querySelector('#pagination')
const alertSuccess = document.querySelector('#alertSuccess')
const rowSuccess =document.querySelector('#rowSuccess')

let stompClient = null;
let username = null;

function connect(event) {
    event.preventDefault();

    spinner.style.display = ''

    username = document.querySelector('#username').value;
    if (username) {
        const socket = new SockJS('/ws');

        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.spin",
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
            spinner.style.display = 'none'
            switch (message.content) {
                case '-1':
                    console.log('error')
                    error.style.display = ''
                    break;
                case '0':
                    console.log('success')
                    success.style.display = ''
                    break
            }
            disconnect()
            break
    }
}

function sendMessage() {
    if (stompClient) {
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(''));
    }
}

function confirmClearFavouritesForm(event) {
    event.preventDefault()
    if (confirm('Вы уверены, что хотите очистить список вакансий?') === true) {
        favouritesTable.style.display = 'none'
        pagination.style.display = 'none'
        alertSuccess.style.display = ''
        rowSuccess.style.display = 'flex'

        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/favourites/clear", true);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
                console.log(xhr.responseText);
            }
        };
        xhr.send();


    } else {
        return false;
    }
}
clearFavouritesForm.addEventListener("submit", confirmClearFavouritesForm);
window.addEventListener('unload', disconnect);
pdfEmailButton.addEventListener('click', connect)
