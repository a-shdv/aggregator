const ctx = document.getElementById('myChart');

new Chart(ctx, {
    type: 'bar',
    data: {
        labels: ['Средняя заработная плата', 'Медианная заработная плата', 'Модальная заработная плата'],
        datasets: [{
            label: 'Статистика по заработной плате в России',
            data: [12, 19, 3, 5, 2, 3],
            borderWidth: 1
        }]
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