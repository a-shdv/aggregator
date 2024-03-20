const ctx = document.getElementById('myChart');
let myChart;

const avgSalary = document.querySelector('#avgSalary');
const medianSalary = document.querySelector('#medianSalary');
const modalSalary = document.querySelector('#modalSalary');

function updateChart() {
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


    if (myChart) {
        myChart.destroy(); // Уничтожаем старый график, чтобы создать новый
    }

    myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Статистика'],
            datasets: [
                {
                    label: 'Средняя ЗП',
                    data: [avgSalaryData],
                    borderColor: '#ffce56',
                    backgroundColor: '#ffdc6c',
                },
                {
                    label: 'Медианная ЗП',
                    data: [medianSalaryData],
                    borderColor: '#ff9f40',
                    backgroundColor: '#ffbc80',
                },
                {
                    label: 'Модальная ЗП',
                    data: [modalSalaryData],
                    borderColor: '#dc5363',
                    backgroundColor: '#dc7181',
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

window.addEventListener('load', updateChart)