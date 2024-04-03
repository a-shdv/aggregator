let myChart;
const myChartCtx = document.getElementById('myChart');

let topTenCitiesChartByVacancies
const topTenCitiesChartByVacanciesCtx = document.getElementById('topTenCitiesChart')

let topTenProfessionsChart
const topTenProfessionsChartCtx = document.getElementById('topTenProfessionsChart')

let sourcesChart
const sourcesChartCtx = document.getElementById('sourcesChart')

let topProfessionsBySalaries
const topProfessionsBySalariesCtx = document.getElementById('topProfessionsBySalariesChart')

function updateMyChart() {
    //region Checks
    const avgSalary = document.querySelector('#avgSalary');
    const medianSalary = document.querySelector('#medianSalary');
    const modalSalary = document.querySelector('#modalSalary');

    let avgSalaryData
    let medianSalaryData
    let modalSalaryData

    if (avgSalary === null) {
        avgSalaryData = 0
    } else {
        avgSalaryData = parseInt(avgSalary.innerText.match(/\d+/g)[0] + avgSalary.innerText.match(/\d+/g)[1]);
    }

    if (medianSalary === null) {
        medianSalaryData = 0
    } else {
        medianSalaryData = parseInt(medianSalary.innerText.match(/\d+/g)[0] + medianSalary.innerText.match(/\d+/g)[1]);
    }

    if (modalSalary === null) {
        modalSalaryData = 0
    } else {
        modalSalaryData = parseInt(modalSalary.innerText.match(/\d+/g)[0] + modalSalary.innerText.match(/\d+/g)[1]);
    }
    //endregion


    if (myChart) {
        myChart.destroy(); // Уничтожаем старый график, чтобы создать новый
    }

    myChart = new Chart(myChartCtx, {
        type: 'bar',
        data: {
            labels: ['Статистика'],
            datasets: [
                {
                    label: 'Средняя ЗП',
                    data: [avgSalaryData],
                    borderColor: 'rgb(255, 159, 64)',
                    backgroundColor: 'rgba(255, 159, 64, 0.2)',
                    borderWidth: 1,
                    fill: false
                },
                {
                    label: 'Медианная ЗП',
                    data: [medianSalaryData],
                    borderColor: 'rgb(255, 205, 86)',
                    backgroundColor: 'rgba(255, 205, 86, 0.2)',
                    borderWidth: 1,
                    fill: false
                },
                {
                    label: 'Модальная ЗП',
                    data: [modalSalaryData],
                    borderColor: 'rgb(255, 99, 132)',
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    borderWidth: 1,
                    fill: false
                }
            ]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

function updateTopTenCitiesByVacanciesChart() {
    if (topTenCitiesChartByVacancies) {
        topTenCitiesChartByVacancies.destroy()
    }

    const data = {
        labels: ['Москва', 'Санкт-Петербург', 'Екатеринбург', 'Новосибирск', 'Краснодар', 'Челябинск', 'Нижний Новгород', 'Казань', 'Красноярск', 'Ростов-на-Дону'],
        datasets: [
            {
                axis: 'y',
                label: ['Количество вакансий'],
                data: [1_083_047, 537_334, 247_926, 213_410, 174_376, 164_687, 154_986, 145_080, 142_857, 131_263],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(255, 159, 64, 0.2)',
                    'rgba(255, 205, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(153, 102, 255, 0.2)',
                    'rgba(201, 203, 207, 0.2)',
                    'rgba(255, 205, 86, 0.2)',
                    'rgba(255, 159, 64, 0.2)',
                    'rgba(255, 99, 132, 0.2)',
                ],
                borderColor: [
                    'rgb(255, 99, 132)',
                    'rgb(255, 159, 64)',
                    'rgb(255, 205, 86)',
                    'rgb(75, 192, 192)',
                    'rgb(54, 162, 235)',
                    'rgb(153, 102, 255)',
                    'rgb(201, 203, 207)',
                    'rgb(255, 205, 86)',
                    'rgb(255, 159, 64)',
                    'rgb(255, 99, 132)',
                ],
                fill: false,
                borderWidth: 1
            }
        ]
    };

    topTenCitiesChartByVacancies = new Chart(topTenCitiesChartByVacanciesCtx, {
        type: 'bar',
        data,
        options: {
            indexAxis: 'y',
        }
    })
}

function updateTopTenProfessionsChart() {
    if (topTenProfessionsChart) {
        topTenProfessionsChart.destroy()
    }

    const data = {
        labels: [
            'Менеджер',
            'Водитель',
            'Продавец',
            'Инженер',
            'Оператор',
            'Слесарь',
            'Менеджер по продажам',
            'Кассир',
            'Врач',
            'Консультант',
            'Другие'
        ],
        datasets: [{
            label: 'Востребованные профессии ',
            data: [4.7, 4.5, 3.5, 2.6, 2.2, 1.9, 1.8, 1.7, 1.4, 1.4, 74.2],
            backgroundColor: [
                'rgb(255, 99, 132)',
                'rgb(255, 159, 64)',
                'rgb(255, 205, 86)',
                'rgb(75, 192, 192)',
                'rgb(54, 162, 235)',
                'rgb(242,64,255)',
                'rgb(153, 102, 255)',
                'rgb(238,255,86)',
                'rgb(103,255,86)',
                'rgb(86,255,196)',
                'rgb(201, 203, 207)',
            ],
            hoverOffset: 1
        }]
    };

    topTenProfessionsChart = new Chart(topTenProfessionsChartCtx, {
        type: 'pie',
        data: data,
    })
}

function updateSourcesChart() {
    if (sourcesChart) {
        sourcesChart.destroy()
    }

    const data = {
        labels: [
            'Рабочий персонал',
            'Продажи',
            'Транспорт, логистика',
            'Производство',
            'Строительство, недвижимость',
            'Инсталляция и сервис',
            'Медицина, фармацевтика',
            'Туризм, гостиницы, рестораны',
            'Бухгалтерия, управленческий учет, финансы предприятия',
            'Прочие',
        ],
        datasets: [{
            label: 'Источники вакансий',
            data: [17.7, 21.7, 21.1, 10.7, 5.9, 4.5, 4.5, 3.7, 3.2, 1.4, 25],
            backgroundColor: [
                'rgb(255, 99, 132)',
                'rgb(255, 159, 64)',
                'rgb(255, 205, 86)',
                'rgb(75, 192, 192)',
                'rgb(54, 162, 235)',
                'rgb(242,64,255)',
                'rgb(153, 102, 255)',
                'rgb(238,255,86)',
                'rgb(103,255,86)',
                'rgb(86,255,196)',
                'rgb(201, 203, 207)',
            ],
            hoverOffset: 1
        }]
    };
    sourcesChart = new Chart(sourcesChartCtx, {
        type: 'pie',
        data: data,
    })
}

function updateProfessionsBySalaries() {
    if (topProfessionsBySalaries) {
        topProfessionsBySalaries.destroy()
    }

    const data = {
        labels: ['Москва', 'Санкт-Петербург', 'Екатеринбург', 'Новосибирск', 'Краснодар', 'Челябинск', 'Нижний Новгород', 'Казань', 'Красноярск', 'Ростов-на-Дону'],
        datasets: [
            {
                axis: 'y',
                label: ['Количество вакансий'],
                data: [1_083_047, 537_334, 247_926, 213_410, 174_376, 164_687, 154_986, 145_080, 142_857, 131_263],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(255, 159, 64, 0.2)',
                    'rgba(255, 205, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(153, 102, 255, 0.2)',
                    'rgba(201, 203, 207, 0.2)',
                    'rgba(255, 205, 86, 0.2)',
                    'rgba(255, 159, 64, 0.2)',
                    'rgba(255, 99, 132, 0.2)',
                ],
                borderColor: [
                    'rgb(255, 99, 132)',
                    'rgb(255, 159, 64)',
                    'rgb(255, 205, 86)',
                    'rgb(75, 192, 192)',
                    'rgb(54, 162, 235)',
                    'rgb(153, 102, 255)',
                    'rgb(201, 203, 207)',
                    'rgb(255, 205, 86)',
                    'rgb(255, 159, 64)',
                    'rgb(255, 99, 132)',
                ],
                fill: false,
                borderWidth: 1
            }
        ]
    };

    topProfessionsBySalaries = new Chart(topProfessionsBySalariesCtx, {
        type: 'bar',
        data,
        options: {
            indexAxis: 'y',
        }
    })
}

window.addEventListener('load', updateMyChart)
window.addEventListener('load', updateTopTenCitiesByVacanciesChart)
window.addEventListener('load', updateTopTenProfessionsChart)
window.addEventListener('load', updateSourcesChart)
window.addEventListener('load', updateProfessionsBySalaries)