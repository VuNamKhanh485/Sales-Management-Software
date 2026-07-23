document.addEventListener('DOMContentLoaded', function() {
    if (typeof window.reportData === 'undefined') return;

    const ctx = document.getElementById('cashFlowChart').getContext('2d');
    let currentChart = null;
    
    const renderChart = (selectedType) => {
        if (currentChart) {
            currentChart.destroy();
        }

        if (selectedType === 'pie') {
            currentChart = new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: ['Doanh thu', 'Chi phí', 'Lợi nhuận'],
                    datasets: [{
                        data: [window.reportData.totalRevenue, window.reportData.totalExpense, window.reportData.totalProfit],
                        backgroundColor: [
                            'rgba(5, 150, 105, 0.7)',
                            'rgba(220, 38, 38, 0.7)',
                            'rgba(37, 99, 235, 0.7)'
                        ],
                        borderColor: [
                            'rgb(5, 150, 105)',
                            'rgb(220, 38, 38)',
                            'rgb(37, 99, 235)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    let label = context.label || '';
                                    if (label) {
                                        label += ': ';
                                    }
                                    if (context.parsed !== null) {
                                        label += new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(context.parsed);
                                    }
                                    return label;
                                }
                            }
                        }
                    }
                }
            });
        } else {
            let revenueType = selectedType === 'line' ? 'line' : 'bar';
            let expenseType = selectedType === 'line' ? 'line' : 'bar';
            let profitType = selectedType === 'bar-only' ? 'bar' : 'line';

            currentChart = new Chart(ctx, {
                type: 'bar', // base type for combination charts
                data: {
                    labels: window.reportData.labels,
                    datasets: [
                        {
                            type: revenueType,
                            label: 'Doanh thu',
                            data: window.reportData.revenue,
                            backgroundColor: 'rgba(5, 150, 105, 0.5)',
                            borderColor: 'rgb(5, 150, 105)',
                            borderWidth: 1,
                            fill: false,
                            tension: 0.1
                        },
                        {
                            type: expenseType,
                            label: 'Chi phí',
                            data: window.reportData.expense,
                            backgroundColor: 'rgba(220, 38, 38, 0.5)',
                            borderColor: 'rgb(220, 38, 38)',
                            borderWidth: 1,
                            fill: false,
                            tension: 0.1
                        },
                        {
                            type: profitType,
                            label: 'Lợi nhuận',
                            data: window.reportData.profit,
                            borderColor: 'rgb(37, 99, 235)',
                            backgroundColor: 'rgba(37, 99, 235, 0.5)',
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
                        x: {
                            ticks: {
                                autoSkip: true,
                                maxTicksLimit: 15,
                                maxRotation: 45,
                                minRotation: 45
                            }
                        },
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
        }
    };

    // Render default chart
    renderChart('bar');

    const chartTypeSelect = document.getElementById('chartTypeSelect');
    if (chartTypeSelect) {
        chartTypeSelect.addEventListener('change', function(e) {
            renderChart(e.target.value);
        });
    }
});
