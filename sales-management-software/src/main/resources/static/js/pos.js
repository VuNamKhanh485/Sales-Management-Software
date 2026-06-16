// =============================================
// 1. Tính tiền thừa
// =============================================
function calcChange() {
    const given = parseFloat(document.getElementById('givenAmount').value) || 0;
    const change = Math.max(0, given - finalAmount);
    document.getElementById('changeDisplay').textContent =
        change.toLocaleString('vi-VN') + 'đ';
}

// =============================================
// 2. Gợi ý số tiền
// =============================================
function generateSuggestions() {
    const container = document.getElementById('amountSuggestions');
    if (!container || finalAmount <= 0) return;

    const suggestions = new Set();
    suggestions.add(finalAmount);

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
// 3. Tìm khách hàng theo SĐT
// =============================================
function findCustomer() {
    const phone = document.getElementById('customerPhone').value.trim();
    if (!phone) return;

    fetch('/pos/api/customer?phone=' + encodeURIComponent(phone))
        .then(res => res.json())
        .then(data => {
            const result = document.getElementById('customerResult');
            if (data.found) {
                result.innerHTML = `
                    <div class="d-flex justify-content-between align-items-center
                                bg-primary bg-opacity-10 rounded p-2 mt-1">
                        <div class="small">
                            <strong>${data.name}</strong> | ${data.phone}
                            <span class="badge bg-primary ms-1">${data.rank}</span>
                            <div class="text-muted">Điểm: ${data.point}</div>
                        </div>
                        <form action="/pos/set-customer" method="POST">
                            <input type="hidden" name="_csrf" value="${getCsrf()}">
                            <input type="hidden" name="customerId" value="${data.id}">
                            <input type="hidden" name="customerName" value="${data.name}">
                            <input type="hidden" name="customerPhone" value="${data.phone}">
                            <button type="submit" class="btn btn-sm btn-primary">Chọn</button>
                        </form>
                    </div>`;
            } else {
                result.innerHTML = `
                    <div class="small text-danger mt-1">
                        <i class="bi bi-exclamation-circle"></i>
                        Không tìm thấy khách hàng
                    </div>`;
            }
        })
        .catch(() => {
            document.getElementById('customerResult').innerHTML =
                '<div class="small text-danger">Lỗi kết nối</div>';
        });
}

// =============================================
// 4. Áp dụng voucher
// =============================================
function applyVoucher() {
    const code = document.getElementById('voucherCode').value.trim();
    if (!code) return;

    fetch('/pos/api/voucher?code=' + encodeURIComponent(code))
        .then(res => res.json())
        .then(data => {
            const msg = document.getElementById('voucherMsg');
            if (data.success) {
                msg.innerHTML = `
                    <span class="text-success">
                        <i class="bi bi-check-circle"></i> ${data.message}
                    </span>`;
                setTimeout(() => location.reload(), 800);
            } else {
                msg.innerHTML = `
                    <span class="text-danger">
                        <i class="bi bi-x-circle"></i> ${data.message}
                    </span>`;
            }
        })
        .catch(() => {
            document.getElementById('voucherMsg').innerHTML =
                '<span class="text-danger">Lỗi kết nối</span>';
        });
}

// =============================================
// 5. Lấy CSRF token
// =============================================
function getCsrf() {
    return document.querySelector('input[name="_csrf"]')?.value || '';
}

// =============================================
// 6. Khởi tạo khi load trang
// =============================================
document.addEventListener('DOMContentLoaded', () => {
    generateSuggestions();
    calcChange();

    // Enter để tìm khách hàng
    document.getElementById('customerPhone')?.addEventListener('keydown', e => {
        if (e.key === 'Enter') {
            e.preventDefault();
            findCustomer();
        }
    });

    // Enter để áp dụng voucher
    document.getElementById('voucherCode')?.addEventListener('keydown', e => {
        if (e.key === 'Enter') {
            e.preventDefault();
            applyVoucher();
        }
    });
});