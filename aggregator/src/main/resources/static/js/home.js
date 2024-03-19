'use strict'

const vacancy = document.querySelector('#vacancy')
const vacancyForm = document.querySelector('#vacancyForm')
const searchVacanciesButton = document.querySelector('#searchVacanciesButton')

const spaceBefore = document.querySelector('#spaceBefore')
const counter = document.querySelector('#counter')
const spinner = document.querySelector('#spinner')
const spinnerInner = document.querySelector('#spinner-inner')
const waitParagraph = document.querySelector('#waitParagraph')
// const progressbar = document.querySelector('#progressbar')
// const progressbarLoader = document.querySelector('#progressbar-loader')
const okButton = document.querySelector('#okButton')
const spaceAfter = document.querySelector('#spaceAfter')

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
            counter.textContent = 'Найдено 0 шт.'
            // progressbarLoader.style.width = '0%'
            sendMessage()
            break
        case 'LEAVE':
            console.log('left')
            break
        case 'RECEIVE':
            let messageCounter = 'Найдено ' + message.content + ' шт.'
            counter.textContent = messageCounter
            // progressbarLoader.style.width = messageCounter
            if (parseInt(message.content) >= 12) {
                spinner.style.display = 'none'
                waitParagraph.style.display = 'none'
                counter.textContent = 'Готово!'
                counter.classList.remove('text-warning')
                counter.classList.add('text-success')
                okButton.style.display = ''
                disconnect()
                setTimeout(() => {
                    counter.style.display = 'none'
                    okButton.style.display = 'none'
                    spaceAfter.style.display = 'none'
                    spaceBefore.style.display = 'none'
                    vacancy.style.display = ''
                }, 5000);
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
    const message = {username, title, salary, onlyWithSalary, experience, cityId, isRemoteAvailable, type}
    if (message && stompClient) {
        // hide vacancy form and show progressbar, counter
        spaceBefore.style.display = ''
        vacancy.style.display = 'none'
        spinner.style.display = ''
        // progressbar.style.display = ''
        counter.style.display = ''
        spaceAfter.style.display = ''

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));
    }
}

window.addEventListener('unload', disconnect);
searchVacanciesButton.addEventListener('click', connect)


document.addEventListener("DOMContentLoaded", () => {
    // Получаем радиобоксы и формы
    const vacancyRadio = document.getElementById("vac");
    const statisticsRadio = document.getElementById("stat");
    const vacancyForm = document.getElementById("vacancyForm");
    const statisticsForm = document.getElementById("statisticsForm");

    // Функция для скрытия формы вакансии и показа формы статистики
    function showStatisticsForm() {
        vacancyForm.style.display = "none";
        statisticsForm.style.display = "block";
    }

    // Функция для скрытия формы статистики и показа формы вакансии
    function showVacancyForm() {
        statisticsForm.style.display = "none";
        vacancyForm.style.display = "";
    }

    // Обработчики событий для радиобоксов
    vacancyRadio.addEventListener("change", function () {
        if (vacancyRadio.checked) {
            showVacancyForm();
        }
    });

    statisticsRadio.addEventListener("change", function () {
        if (statisticsRadio.checked) {
            showStatisticsForm();
        }
    });

    // Обработчик события для кнопки переключения форм
    const switchFormsButton = document.getElementById("switchFormsButton");
    switchFormsButton.addEventListener("click", function () {
        if (vacancyForm.style.display === "none") {
            showVacancyForm();
            vacancyRadio.checked = true;
        } else {
            showStatisticsForm();
            statisticsRadio.checked = true;
        }
    });

    // По умолчанию показываем форму вакансии
    showVacancyForm();
});