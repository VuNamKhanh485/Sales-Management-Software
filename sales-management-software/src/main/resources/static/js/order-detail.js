function downloadPdf() {
    const element = document.getElementById('receiptContent');
    if (!element) return;

    // Use global orderCode or fallback to 'SMS'
    const name = typeof orderCode !== 'undefined' ? orderCode : 'SMS';

    const opt = {
        margin:       [10, 10, 10, 10],
        filename:     'HOADON_' + name + '.pdf',
        image:        { type: 'jpeg', quality: 0.98 },
        html2canvas:  { scale: 2, useCORS: true, letterRendering: true },
        jsPDF:        { unit: 'mm', format: 'a4', orientation: 'portrait' }
    };

    html2pdf().set(opt).from(element).save();
}

function printReceipt() {
    window.print();
}

// Tự động kích hoạt khi được mở từ tab mới kèm tham số
document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const name = typeof orderCode !== 'undefined' ? orderCode : 'SMS';

    if (urlParams.get('downloadPdf') === 'true') {
        const element = document.getElementById('receiptContent');
        if (element) {
            const opt = {
                margin:       [10, 10, 10, 10],
                filename:     'HOADON_' + name + '.pdf',
                image:        { type: 'jpeg', quality: 0.98 },
                html2canvas:  { scale: 2, useCORS: true, letterRendering: true },
                jsPDF:        { unit: 'mm', format: 'a4', orientation: 'portrait' }
            };
            html2pdf().set(opt).from(element).save().then(() => {
                setTimeout(() => window.close(), 1500);
            });
        }
    } else if (urlParams.get('print') === 'true') {
        window.print();
        window.addEventListener('afterprint', () => {
            window.close();
        });
        setTimeout(() => window.close(), 5000);
    }
});
