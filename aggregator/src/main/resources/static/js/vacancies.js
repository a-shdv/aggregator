const vacanciesForm = document.querySelector('#clearVacanciesForm')
const alertSuccess = document.querySelector('#alertSuccess')
const allVacanciesHeader = document.querySelector('#allVacanciesHeader')
const allVacanciesList = document.querySelector('#allVacanciesList')
const clearVacanciesForm = document.querySelector('#clearVacanciesForm')
const clearVacanciesButton = document.querySelector('#clearVacanciesButton')
const pagination = document.querySelector('#pagination')
const spaceTest = document.querySelector('#spaceTest')

function confirmClearVacanciesButton(event) {
    event.preventDefault()
    const test = confirm('Вы уверены, что хотите очистить список вакансий?')
    if (test === true) {
        alertSuccess.style.display = ''
        spaceTest.style.display = ''
        allVacanciesHeader.style.display = 'none'
        allVacanciesList.style.display = 'none'
        clearVacanciesForm.style.display = 'none'
        pagination.style.display = 'none'

        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/vacancies/clear", true);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
                console.log(xhr.responseText);
            }
        };
        xhr.send();

        setTimeout(() => {
            alertSuccess.style.display = 'none'
            spaceTest.style.display = 'none'
            document.getElementById('alertWarningMock').style.display = ''
        }, 1500);

    } else {
        console.log('false')
        return false;
    }
}

clearVacanciesForm.addEventListener("submit", confirmClearVacanciesButton);