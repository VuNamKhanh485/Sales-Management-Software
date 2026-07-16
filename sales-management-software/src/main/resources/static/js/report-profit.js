document.addEventListener('DOMContentLoaded', function() {
    if (typeof window.reportData === 'undefined') return;

    const ctx = document.getElementById('cashFlowChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: window.reportData.labels,
            datasets: [
                {
                    label: 'Doanh thu',
                    data: window.reportData.revenue,
                    backgroundColor: 'rgba(5, 150, 105, 0.5)',
                    borderColor: 'rgb(5, 150, 105)',
                    borderWidth: 1
                },
                {
                    label: 'Chi phí',
                    data: window.reportData.expense,
                    backgroundColor: 'rgba(220, 38, 38, 0.5)',
                    borderColor: 'rgb(220, 38, 38)',
                    borderWidth: 1
                },
                {
                    type: 'line',
                    label: 'Lợi nhuận',
                    data: window.reportData.profit,
                    borderColor: 'rgb(37, 99, 235)',
                    backgroundColor: 'rgb(37, 99, 235)',
                    borderWidth: 2,
                    fill: false,
                    tension: 0.1
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
                        }
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            let label = context.dataset.label || '';
                            if (label) {
                                label += ': ';
                            }
                            if (context.parsed.y !== null) {
                                label += new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(context.parsed.y);
                            }
                            return label;
                        }
                    }
                }
            }
        }
    });
});
