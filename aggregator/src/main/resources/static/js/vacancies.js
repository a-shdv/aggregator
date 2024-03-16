const vacanciesForm = document.querySelector('#clearVacanciesForm')

function confirmClearVacanciesForm() {
    if (confirm('Вы уверены, что хотите очистить список вакансий?')) {
        vacanciesForm.submit();
    } else {
        return false;
    }
}