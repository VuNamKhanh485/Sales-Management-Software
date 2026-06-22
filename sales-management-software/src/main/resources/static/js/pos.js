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
// 4. Tìm khách hàng theo SĐT
// =============================================
function findCustomer() {
    const phone = document.getElementById('customerPhone')?.value.trim();
    if (!phone) return;

    fetch('/pos/api/customer?phone=' + encodeURIComponent(phone))
        .then(r => r.json())
        .then(data => {
            const result = document.getElementById('customerResult');
            if (data.found) {
                result.innerHTML = `
                    <div class="d-flex justify-content-between align-items-center
                                bg-primary bg-opacity-10 rounded p-2 mt-1">
                        <div class="small">
                            <strong>${data.name}</strong> — ${data.phone}
                            <span class="badge bg-primary ms-1">${data.rank}</span>
                            <div class="text-muted">Điểm: ${data.point}</div>
                        </div>
                        <a href="/pos/set-customer?customerId=${data.id}&customerName=${encodeURIComponent(data.name)}&customerPhone=${encodeURIComponent(data.phone)}"
                           class="btn btn-sm btn-primary">Chọn</a>
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
// 5. Thêm khách hàng mới inline
// =============================================
function saveNewCustomer() {
    const name    = document.getElementById('nc_name').value.trim();
    const phone   = document.getElementById('nc_phone').value.trim();
    const email   = document.getElementById('nc_email').value.trim();
    const address = document.getElementById('nc_address').value.trim();
    const msg     = document.getElementById('newCustomerMsg');

    if (!name || !phone) {
        msg.innerHTML = '<div class="alert alert-danger py-1 small">Vui lòng nhập họ tên và SĐT!</div>';
        return;
    }

    const params = new URLSearchParams({ fullName: name, phone, email, address });
    fetch('/customers/api/quick-create?' + params.toString())
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                msg.innerHTML = '<div class="alert alert-success py-1 small">Thêm thành công!</div>';
                setTimeout(() => {
                    window.location.href =
                        `/pos/set-customer?customerId=${data.id}` +
                        `&customerName=${encodeURIComponent(data.name)}` +
                        `&customerPhone=${encodeURIComponent(data.phone)}`;
                }, 800);
            } else {
                msg.innerHTML = `<div class="alert alert-danger py-1 small">${data.message}</div>`;
            }
        })
        .catch(() => {
            msg.innerHTML = '<div class="alert alert-danger py-1 small">Lỗi kết nối!</div>';
        });
}

// =============================================
// 6. Tải và tìm sản phẩm trong modal
// =============================================
function searchProducts() {
    const keyword    = document.getElementById('modalKeyword').value.trim();
    const categoryId = document.getElementById('modalCategory').value;
    const grid       = document.getElementById('productGrid');

    grid.innerHTML = '<div class="col-12 text-center py-3">' +
        '<div class="spinner-border spinner-border-sm text-primary"></div></div>';

    let url = '/pos/api/products?';
    if (keyword)    url += 'keyword='    + encodeURIComponent(keyword) + '&';
    if (categoryId) url += 'categoryId=' + categoryId;

    fetch(url)
        .then(r => r.json())
        .then(products => {
            if (products.length === 0) {
                grid.innerHTML =
                    '<div class="col-12 text-center text-muted py-3">Không tìm thấy sản phẩm</div>';
                return;
            }
            grid.innerHTML = products.map(p => `
                <div class="col-6 col-md-3 col-lg-2">
                    <div class="product-card-modal" onclick="addProduct(${p.id})">
                        <img src="${p.imageUrl || 'https://via.placeholder.com/150x80?text=No+Image'}"
                             alt="${p.name}">
                        <div class="p-2">
                            <div class="small fw-semibold text-truncate">${p.name}</div>
                            <div class="text-muted" style="font-size:10px">Tồn: ${p.stock != null ? p.stock : 0} | ${p.sku}</div>
                            <div class="text-primary fw-bold small">
                                ${Number(p.price).toLocaleString('vi-VN')}đ
                            </div>
                        </div>
                    </div>
                </div>
            `).join('');
        })
        .catch(() => {
            grid.innerHTML =
                '<div class="col-12 text-center text-danger py-3">Lỗi tải sản phẩm</div>';
        });
}

function addProduct(productUnitId) {
    window.location.href = '/pos/add-by-id?productUnitId=' + productUnitId;
}

// =============================================
// 7. Voucher từ panel phải
// =============================================
function applyVoucher() {
    const code = document.getElementById('voucherCode')?.value.trim();
    if (!code) return;
    applyVoucherByCode(code, 'voucherMsg');
}

// =============================================
// 8. Voucher từ modal khuyến mãi
// =============================================
function applyVoucherFromModal() {
    const code = document.getElementById('modalVoucherCode').value.trim();
    if (!code) return;
    applyVoucherByCode(code, 'modalVoucherMsg');
}

function applyVoucherByCode(code, msgElementId) {
    const msg = document.getElementById(msgElementId);
    fetch('/pos/api/voucher?code=' + encodeURIComponent(code))
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                msg.innerHTML = `<span class="text-success">
                    <i class="bi bi-check-circle"></i> ${data.message}</span>`;
                setTimeout(() => location.reload(), 800);
            } else {
                msg.innerHTML = `<span class="text-danger">
                    <i class="bi bi-x-circle"></i> ${data.message}</span>`;
            }
        })
        .catch(() => {
            msg.innerHTML = '<span class="text-danger">Lỗi kết nối</span>';
        });
}

// =============================================
// 9. Khởi tạo khi load trang
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

    // Enter tìm khách hàng
    document.getElementById('customerPhone')
        ?.addEventListener('keydown', e => {
            if (e.key === 'Enter') { e.preventDefault(); findCustomer(); }
        });

    // Enter tìm sản phẩm trong modal
    document.getElementById('modalKeyword')
        ?.addEventListener('keydown', e => {
            if (e.key === 'Enter') { e.preventDefault(); searchProducts(); }
        });

    // Tải sản phẩm khi mở modal
    document.getElementById('productModal')
        ?.addEventListener('show.bs.modal', () => {
            searchProducts();
        });
});

// =============================================
// 10. Xuất PDF hóa đơn
// =============================================
function downloadReceiptPdf() {
    const element = document.getElementById('receiptPrintArea');
    if (!element) return;

    const filename = 'HOADON_' + (successOrderCode || 'SMS') + '.pdf';
    
    // Cấu hình html2pdf để xuất khổ giấy a5 dọc
    const opt = {
        margin:       [5, 5, 5, 5],
        filename:     filename,
        image:        { type: 'jpeg', quality: 0.98 },
        html2canvas:  { scale: 2, useCORS: true, letterRendering: true },
        jsPDF:        { unit: 'mm', format: 'a5', orientation: 'portrait' }
    };

    html2pdf().set(opt).from(element).save();
}