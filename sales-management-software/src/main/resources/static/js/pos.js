// =============================================
// Cấu hình — đọc từ DOM (#pos-config)
// =============================================
let finalAmount = 0;
let showSuccessModal = false;
let successOrderCode = '';

function loadConfig() {
    const configEl = document.getElementById('pos-config');
    if (configEl) {
        finalAmount = parseFloat(configEl.getAttribute('data-final-amount')) || 0;
        showSuccessModal = configEl.getAttribute('data-show-success-modal') === 'true';
        successOrderCode = configEl.getAttribute('data-success-order-code') || '';
    }
}

// =============================================
// Đóng modal khách hàng (gọi từ iframe popup)
// =============================================
function closeCustomerModal() {
    const modalEl = document.getElementById('newCustomerModal');
    if (modalEl) {
        const modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
        modal.hide();
    }
}

// Lắng nghe CUSTOMER_CREATED từ iframe popup
window.addEventListener('message', function(event) {
    const data = event.data;
    if (!data || !data.type) return;
    if (data.type === 'CUSTOMER_CREATED') {
        closeCustomerModal();
        const cust = data.data;
        window.location.href = `/pos/set-customer?customerId=${cust.id}&customerName=${encodeURIComponent(cust.fullName)}&customerPhone=${encodeURIComponent(cust.phone)}`;
    }
});

// =============================================
// Tính tiền thừa (real-time)
// =============================================
function calcChange() {
    const given = parseFloat(document.getElementById('givenAmount')?.value) || 0;
    const change = Math.max(0, given - finalAmount);
    const el = document.getElementById('changeDisplay');
    if (el) el.textContent = change.toLocaleString('vi-VN') + 'đ';
}

// =============================================
// Gợi ý số tiền khách đưa
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
// Xuất PDF hóa đơn
// =============================================
function downloadReceiptPdf() {
    const element = document.getElementById('receiptPrintArea');
    if (!element) return;
    const filename = 'HOADON_' + (successOrderCode || 'SMS') + '.pdf';
    html2pdf().set({
        margin: [5, 5, 5, 5],
        filename: filename,
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { scale: 2, useCORS: true, letterRendering: true },
        jsPDF: { unit: 'mm', format: 'a5', orientation: 'portrait' }
    }).from(element).save();
}

// =============================================
// Init
// =============================================
document.addEventListener('DOMContentLoaded', () => {
    loadConfig();
    generateSuggestions();
    calcChange();

    // Tự động hiển thị hóa đơn thành công nếu có
    if (showSuccessModal) {
        const modalEl = document.getElementById('successReceiptModal');
        if (modalEl) {
            const successModal = new bootstrap.Modal(modalEl);
            successModal.show();
        }
    }
});