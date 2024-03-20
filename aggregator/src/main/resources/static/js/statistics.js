const ctx = document.getElementById('myChart');

avgSalary = document.querySelector('#avgSalary')
medianSalary = document.querySelector('#medianSalary')
modalSalary = document.querySelector('#modalSalary')
new Chart(ctx, {
    type: 'bar',
    data: {
        labels: ['Статистика'],
        datasets: [
            {
                label: 'Средняя ЗП',
                data: [1],
                borderColor: '#36A2EB',
                backgroundColor: '#9BD0F5',
            },
            {
                label: 'Медианная ЗП',
                data: [2],
                borderColor: '#ffce56',
                backgroundColor: '#ffdc6c',
            },
            {
                label: 'Модальная ЗП',
                data: [3],
                borderColor: '#ff9f40',
                backgroundColor: '#ffbc80',
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

myChart = document.querySelector('#myChart')
isRequestEmpty = document.querySelector('#isRequestEmpty')
if (isRequestEmpty != null) {
    myChart.style.display = 'none'
}