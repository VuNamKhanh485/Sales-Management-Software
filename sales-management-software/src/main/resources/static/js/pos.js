// =============================================
// Biến global — được set từ Thymeleaf inline
// =============================================
// const finalAmount = ... (set trong pos.html)

// =============================================
// 1. Đóng đơn hàng
// =============================================
function confirmCloseOrder(index, e) {
    e.preventDefault();
    e.stopPropagation();
    if (confirm('Đóng Đơn ' + index + '? Dữ liệu sẽ bị xóa.')) {
        window.location.href = '/pos/close/' + index;
    }
}

// =============================================
// 1b. Xóa toàn bộ đơn hàng hiện tại
// =============================================
function clearCurrentOrder(e) {
    e.preventDefault();
    if (confirm('Xóa toàn bộ đơn hàng hiện tại?')) {
        window.location.href = '/pos/clear';
    }
}

// =============================================
// 2. Tính tiền thừa
// =============================================
function calcChange() {
    const given = parseFloat(document.getElementById('givenAmount')?.value) || 0;
    const change = Math.max(0, given - finalAmount);
    const el = document.getElementById('changeDisplay');
    if (el) el.textContent = change.toLocaleString('vi-VN') + 'đ';
}

// =============================================
// 3. Gợi ý số tiền
// =============================================
function generateSuggestions() {
    const container = document.getElementById('amountSuggestions');
    if (!container || finalAmount <= 0) return;

    const suggestions = new Set([finalAmount]);
    const rounds = [1000, 2000, 5000, 10000, 20000, 50000, 100000];
    for (const r of rounds) {
        const rounded = Math.ceil(finalAmount / r) * r;
        if (rounded > finalAmount) suggestions.add(rounded);
        if (suggestions.size >= 6) break;
    }

    container.innerHTML = '';
    suggestions.forEach(amount => {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'btn btn-outline-primary btn-sm amount-btn';
        btn.textContent = amount.toLocaleString('vi-VN') + 'đ';
        btn.onclick = () => {
            document.getElementById('givenAmount').value = amount;
            calcChange();
        };
        container.appendChild(btn);
    });
}

// =============================================
// Khởi tạo khi load trang
// =============================================
document.addEventListener('DOMContentLoaded', () => {
    generateSuggestions();
    calcChange();

    // Tự động hiển thị hóa đơn thành công nếu có
    if (typeof showSuccessModal !== 'undefined' && showSuccessModal) {
        const modalEl = document.getElementById('successReceiptModal');
        if (modalEl) {
            const successModal = new bootstrap.Modal(modalEl);
            successModal.show();
        }
    }
});

// =============================================
// Xuất PDF hóa đơn
// =============================================
function downloadReceiptPdf() {
    const element = document.getElementById('receiptPrintArea');
    if (!element) return;

    const filename = 'HOADON_' + (successOrderCode || 'SMS') + '.pdf';

    const opt = {
        margin: [5, 5, 5, 5],
        filename: filename,
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { scale: 2, useCORS: true, letterRendering: true },
        jsPDF: { unit: 'mm', format: 'a5', orientation: 'portrait' }
    };

    html2pdf().set(opt).from(element).save();
}