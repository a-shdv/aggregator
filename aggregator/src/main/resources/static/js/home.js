'use strict'

const vacancy = document.querySelector('#vacancy')
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
            counter.textContent = '0%'
            // progressbarLoader.style.width = '0%'
            sendMessage()
            break
        case 'LEAVE':
            console.log('left')
            break
        case 'RECEIVE':
            let messageCounter = parseInt(message.content).toFixed(0) + '%'
            counter.textContent = messageCounter
            // progressbarLoader.style.width = messageCounter
            if (parseInt(message.content) >= 100) {
                spinner.style.display = 'none'
                waitParagraph.style.display = 'none'
                counter.textContent = 'Готово!'
                counter.classList.remove('text-warning')
                counter.classList.add('text-success')
                document.getElementById('cancelForm').style.display = 'none';
                okButton.style.display = ''
                window.onbeforeunload = null;
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
        document.querySelector('#cancelForm').style.display = ''
        // progressbar.style.display = ''
        // document.querySelector('#can/celButton').style.display = ''
        counter.style.display = ''
        spaceAfter.style.display = ''

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));
    }
}

window.addEventListener('unload', disconnect);
if (searchVacanciesButton != null) {
    searchVacanciesButton.addEventListener('click', connect)
}

const statisticsFormSubmit = document.getElementById('statisticsFormSubmit')
if (statisticsFormSubmit != null) {
    statisticsFormSubmit.addEventListener('click', (event) => {
        // if (document.getElementById('profession').value == null
        //     || document.getElementById('city').value == null) {
        //     return
        // }

        event.preventDefault(); // Prevent form submission
        let form = document.getElementById('statisticsForm');
        let formData = new FormData(form); // Создаем объект FormData из формы

        let xhr = new XMLHttpRequest(); // Создаем объект XMLHttpRequest
        xhr.open('POST', form.action, true); // Настраиваем запрос
        xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE) { // Проверяем, завершен ли запрос
                if (xhr.status === 200) { // Проверяем успешность запроса
                    console.log('Данные успешно отправлены на сервер');
                    // Дополнительные действия при успешной отправке данных
                } else {
                    console.error('Произошла ошибка при отправке данных:', xhr.statusText);
                }
            }
        };

        xhr.send(formData); // Отправляем данные формы на сервер

        // Скрыть форму статистики
        document.getElementById('statisticsForm').style.display = 'none';
        // document.getElementById('switchForms').style.display = 'none'

        // Показать сообщение об успешной отправке
        document.querySelector('#cancelForm').style.display = 'none'
        document.getElementById('spaceAfterAlertSuccess').style.display = '';


        document.getElementById('alertSuccess').style.display = '';
        // Установить таймер на 2 секунды для скрытия сообщения об успехе и возврата формы
        setTimeout(() => {
            // Скрыть сообщение об успешной отправке
            document.getElementById('alertSuccess').style.display = 'none';
            document.getElementById('spaceAfterAlertSuccess').style.display = 'none';

            // Вернуть форму статистики
            // document.getElementById('switchForms').style.display = ''
            document.getElementById('statisticsForm').style.display = '';
        }, 2000);
    });
}

const toggleCollapseVacancies = document.getElementById("toggleCollapse")
if (toggleCollapseVacancies != null) {
    toggleCollapseVacancies.addEventListener("click", function (e) {
        e.preventDefault();
        const collapsibleSections = document.querySelectorAll(".collapsible");
        collapsibleSections.forEach(function (section) {
            section.classList.toggle("collapsed");
            if (section.classList.contains("collapsed")) {
                document.getElementById('searchVacanciesButtonSpace').style.display = ''
                section.style.maxHeight = "0";
                section.style.opacity = "0";
                this.innerText = "Show";
                document.getElementById('search')
            } else {
                document.getElementById('searchVacanciesButtonSpace').style.display = 'none'
                section.style.maxHeight = "none";
                section.style.opacity = "1";
                this.innerText = "Hide";
            }
        });
    });
}

const toggleCollapseStatistics = document.getElementById('toggleCollapseStatistics')
if (toggleCollapseStatistics != null) {
    toggleCollapseStatistics.addEventListener('click', (e) => {
        e.preventDefault()
        const collapsibleSections = document.querySelectorAll(".collapsible");
        collapsibleSections.forEach((section) => {
            section.classList.toggle("collapsed");
            if (section.classList.contains("collapsed")) {
                section.style.maxHeight = "0";
                section.style.opacity = "0";
                this.innerText = "Show";
                document.getElementById('searchStatisticsButtonSpace').style.display = ''
            } else {
                section.style.maxHeight = "none";
                section.style.opacity = "1";
                this.innerText = "Hide";
                document.getElementById('searchStatisticsButtonSpace').style.display = 'none'
            }
        });
    })
}