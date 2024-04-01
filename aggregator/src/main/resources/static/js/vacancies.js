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
const addToFavouritesSubmitButtons = document.querySelectorAll('.addToFavouritesSubmitButton');
const favouriteForms = document.querySelectorAll('#favouriteForm');
const alertFavouriteAddedSuccessfully = document.querySelector('#alertFavouriteAddedSuccessfully');
const alertFavouriteAlreadyExists = document.querySelector('#alertFavouriteAlreadyExists');

favouriteForms.forEach((form, index) => {
    form.addEventListener('submit', (event) => {
        event.preventDefault()

        alertFavouriteAddedSuccessfully.style.display = 'none'
        alertFavouriteAlreadyExists.style.display = 'none'

        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/favourites', true);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    console.log(xhr.responseText);
                    alertFavouriteAddedSuccessfully.style.display = ''
                } else {
                    console.log('error');
                    alertFavouriteAlreadyExists.style.display = ''
                }
            }
        };

        xhr.send(new FormData(form));

        setTimeout(() => {
            alertFavouriteAddedSuccessfully.style.display = 'none'
            alertFavouriteAlreadyExists.style.display = 'none'
        }, 1000);
    })
})

document.addEventListener('DOMContentLoaded', function() {
    const pageSizeSelect = document.getElementById('pageSizeSelect');
    pageSizeSelect.addEventListener('change', function() {
        const selectedPageSize = this.value;
        const url = window.location.pathname + '?size=' + selectedPageSize;
        window.location.href = url;
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const pageSizeSelect = document.getElementById('pageSizeSelect');

    // Проверяем, есть ли сохраненное значение в localStorage
    const savedValue = localStorage.getItem('selectedPageSize');
    if (savedValue) {
        pageSizeSelect.value = savedValue;
    }

    pageSizeSelect.addEventListener('change', function() {
        const selectedPageSize = this.value;
        localStorage.setItem('selectedPageSize', selectedPageSize); // Сохраняем выбранное значение
        const url = window.location.pathname + '?size=' + selectedPageSize;
        window.location.href = url;
    });
});