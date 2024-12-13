async function drawChart() {
    const chart = document.getElementById('chart')
    const id = chart.getAttribute('value')
    const response = await fetch(`/api/admin/vote?songId=${id}`)

    if (response.ok) {
        let content = await response.json()

        const data = {
            labels: content['time'],
            datasets: [{
                label: '投票数',
                data: content['votes'],
                fill: false,
                borderColor: 'rgb(75, 192, 192)', // 设置线的颜色
                tension: 0.1
            }]
        }

        const config = {
            type: 'line',
            data: data,
        };

        new Chart(chart, config);
    }
}

window.onload = drawChart