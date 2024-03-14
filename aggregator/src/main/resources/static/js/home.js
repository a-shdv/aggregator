'use strict';
let stompClient = null;

const vacancy = document.querySelector('#vacancy')
const progressBar = document.querySelector('#progressbar')
const progressBarLoader = document.querySelector('#progressbar-loader')
const spaceBefore = document.querySelector('#spaceBefore')
const spaceAfter = document.querySelector('#spaceAfter')
const buttonCancelSearch = document.querySelector('#buttonCancelSearch')
const buttonOk = document.querySelector('#buttonOk')

document.querySelector('#vacancyForm').addEventListener('submit', connect, true)

function connect() {
    event.preventDefault()
    // document.querySelector('#progressbar').style.display = 'block'
    let socket = new SockJS('/aggregator');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, frame => {
        console.log('Connected: ' + frame)

        // ON RECEIVE
        stompClient.subscribe('/topic/progressbar', response => {
            let message = JSON.parse(response.body);
            console.log('received ' + message)
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
        // vacancy.style.display = 'none'
        // spaceBefore.style.height = '50vh'
        // spaceAfter.style.height = '50vh'
        progressBar.style.display = ''
        buttonCancelSearch.style.display = ''

        stompClient.send("/app/toJava", {}, JSON.stringify({
            username: document.querySelector("#username").value,
            title: document.querySelector("#title").value,
            salary: document.querySelector('#salary').value,
            onlyWithSalary: document.querySelector('#onlyWithSalary').checked,
            experience: parseInt(document.querySelector('input[name="experience"]:checked').value),
            cityId: parseInt(document.querySelector('#cityId').value),
            isRemoteAvailable: document.querySelector('#isRemoteAvailable').checked
        }));
    }, error => {
        console.error('Error during WebSocket connection: ' + error);
        // document.querySelector('#progressbar').style.display = 'none';
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect()
        console.log("Disconnected")
    }
}

// Selecting form elements
const titleInput = document.getElementById('title');
const salaryInput = document.getElementById('salary');
const onlyWithSalaryCheckbox = document.getElementById('onlyWithSalary');
const experienceRadios = document.getElementsByName('experience');
const citySelect = document.getElementById('cityId');
const isRemoteAvailableCheckbox = document.getElementById('isRemoteAvailable');

// Adding event listeners to form elements
titleInput.addEventListener('input', saveFormValues);
salaryInput.addEventListener('input', saveFormValues);
onlyWithSalaryCheckbox.addEventListener('change', saveFormValues);
for (let i = 0; i < experienceRadios.length; i++) {
    experienceRadios[i].addEventListener('change', saveFormValues);
}
citySelect.addEventListener('change', saveFormValues);
isRemoteAvailableCheckbox.addEventListener('change', saveFormValues);

// Function to save form field values in local storage
function saveFormValues() {
    // Saving values to local storage
    localStorage.setItem('title', titleInput.value);
    localStorage.setItem('salary', salaryInput.value);
    localStorage.setItem('onlyWithSalary', onlyWithSalaryCheckbox.checked);
    for (let i = 0; i < experienceRadios.length; i++) {
        if (experienceRadios[i].checked) {
            localStorage.setItem('experience', experienceRadios[i].value);
            break;
        }
    }
    localStorage.setItem('cityId', citySelect.value);
    localStorage.setItem('isRemoteAvailable', isRemoteAvailableCheckbox.checked);
}

// Function to load form field values from local storage
function loadFormValues() {
    // Loading values from local storage
    titleInput.value = localStorage.getItem('title') || '';
    salaryInput.value = localStorage.getItem('salary') || '0';
    onlyWithSalaryCheckbox.checked = localStorage.getItem('onlyWithSalary') === 'true';
    const experienceValue = localStorage.getItem('experience');
    for (let i = 0; i < experienceRadios.length; i++) {
        if (experienceRadios[i].value === experienceValue) {
            experienceRadios[i].checked = true;
            break;
        }
    }
    citySelect.value = localStorage.getItem('cityId') || '0';
    isRemoteAvailableCheckbox.checked = localStorage.getItem('isRemoteAvailable') === 'true';
}

window.addEventListener('load', loadFormValues);

function clearVacancies() {
    confirm("Вы уверены, что хотите очистить список вакансий?")
}