const spinner = document.querySelector('#spinner')
const pdfEmailButton = document.querySelector('#pdfEmailButton')
const error = document.querySelector('#error')
const success = document.querySelector('#success')

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
    // window.removeEventListener('unload')
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
    // window.onbeforeunload = () => "You have attempted to leave this page.  If you have made any changes to the fields without clicking the Save button, your changes will be lost.  Are you sure you want to exit this page?";
    //
    // const title = document.querySelector("#title").value
    // const salary = document.querySelector('#salary').value
    // const onlyWithSalary = document.querySelector('#onlyWithSalary').checked
    // const experience = parseInt(document.querySelector('input[name="experience"]:checked').value)
    // const cityId = parseInt(document.querySelector('#cityId').value)
    // const isRemoteAvailable = document.querySelector('#isRemoteAvailable').checked
    // const type = 'CHAT'
    // const message = {username, title, salary, onlyWithSalary, experience, cityId, isRemoteAvailable, type}
    // if (message && stompClient) {
    //     // hide vacancy form and show progressbar, counter
    //     vacancy.style.display = 'none'
    //     progressbar.style.display = ''
    //     counter.style.display = ''
    //
    //     stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));
    // }
}

// function sendLeaveMessage() {
//     if (stompClient) {
//         stompClient.send("/app/chat.sendMessage", {}, JSON.stringify('LEAVE'));
//     }
// }
//
window.addEventListener('unload', disconnect);
pdfEmailButton.addEventListener('click', connect)