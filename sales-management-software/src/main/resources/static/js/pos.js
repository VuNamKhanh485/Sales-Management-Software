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

function findCustomer() {
    const phone = document.getElementById('customerPhone')?.value.trim();
    if (!phone) return;

    fetch('/pos/data/customer?phone=' + encodeURIComponent(phone))
        .then(r => r.json())
        .then(list => {
            const result = document.getElementById('customerResult');
            if (list && list.length > 0) {
                let html = '<div class="list-group mt-1 shadow-sm" style="max-height: 200px; overflow-y: auto;">';
                list.forEach(c => {
                    html += `
                        <div class="list-group-item list-group-item-action d-flex justify-content-between align-items-center p-2 border-bottom">
                            <div class="small text-start">
                                <div class="fw-bold text-dark">${c.name}</div>
                                <div class="text-muted" style="font-size: 11px;">SĐT: ${c.phone} | Hạng: <span class="badge bg-info text-dark">${c.rank}</span></div>
                            </div>
                            <a href="/pos/set-customer?customerId=${c.id}&customerName=${encodeURIComponent(c.name)}&customerPhone=${encodeURIComponent(c.phone)}"
                               class="btn btn-sm btn-primary py-1 px-2 fw-semibold" style="font-size: 11px;">Chọn</a>
                        </div>`;
                });
                html += '</div>';
                result.innerHTML = html;
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
                '<div class="small text-danger">Lỗi kết nối khi tìm khách hàng</div>';
        });
}

// =============================================
// 5. Thêm khách hàng mới inline
// =============================================
function saveNewCustomer() {
    const name = document.getElementById('nc_name').value.trim();
    const phone = document.getElementById('nc_phone').value.trim();
    const email = document.getElementById('nc_email').value.trim();
    const address = document.getElementById('nc_address').value.trim();
    const msg = document.getElementById('newCustomerMsg');

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
    const keyword = document.getElementById('modalKeyword').value.trim();
    const categoryId = document.getElementById('modalCategory').value;
    const grid = document.getElementById('productGrid');

    grid.innerHTML = '<div class="col-12 text-center py-3">' +
        '<div class="spinner-border spinner-border-sm text-primary"></div></div>';

    let url = '/pos/data/products?';
    if (keyword) url += 'keyword=' + encodeURIComponent(keyword) + '&';
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
                            <div class="text-muted" style="font-size:10px">${p.sku}</div>
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

    // Tải danh sách voucher khi mở modal
    document.getElementById('voucherModal')
        ?.addEventListener('show.bs.modal', () => {
            loadAvailableVouchers();
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
        margin: [5, 5, 5, 5],
        filename: filename,
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { scale: 2, useCORS: true, letterRendering: true },
        jsPDF: { unit: 'mm', format: 'a5', orientation: 'portrait' }
    };

    html2pdf().set(opt).from(element).save();
}

// =============================================
// 11. Tải danh sách voucher khả dụng theo khách hàng
// =============================================
function loadAvailableVouchers() {
    const listContainer = document.getElementById('availableVouchersList');
    if (!listContainer) return;

    listContainer.innerHTML = '<div class="text-center py-3"><div class="spinner-border spinner-border-sm text-primary"></div></div>';

    let url = '/pos/data/vouchers';
    if (typeof customerId !== 'undefined' && customerId !== null) {
        url += '?customerId=' + customerId;
    }

    fetch(url)
        .then(r => r.json())
        .then(vouchers => {
            if (vouchers.length === 0) {
                listContainer.innerHTML = '<div class="text-center text-muted py-3 small">Không có khuyến mãi nào phù hợp với hạng thẻ của bạn</div>';
                return;
            }

            listContainer.innerHTML = vouchers.map(v => {
                const discountText = v.discountType === 'PERCENT'
                    ? `Giảm ${parseFloat(v.discountValue)}%` + (v.maxDiscountAmount ? ` (Tối đa ${Number(v.maxDiscountAmount).toLocaleString('vi-VN')}đ)` : '')
                    : `Giảm ${Number(v.discountValue).toLocaleString('vi-VN')}đ`;

                const minOrderText = `Đơn từ ${Number(v.minOrderAmount).toLocaleString('vi-VN')}đ`;
                const rankText = v.rankName && v.rankName !== 'Mọi khách hàng'
                    ? `<span class="badge bg-danger ms-2">${v.rankName}</span>`
                    : '<span class="badge bg-secondary ms-2">Mọi thành viên</span>';

                return `
                    <div class="list-group-item p-2 mb-2 rounded border bg-white shadow-sm d-flex justify-content-between align-items-center">
                        <div style="flex-grow: 1; padding-right: 8px;">
                            <div class="d-flex align-items-center">
                                <span class="badge bg-success font-monospace" style="font-size: 12px; letter-spacing: 0.5px;">${v.code}</span>
                                ${rankText}
                            </div>
                            <div class="fw-semibold text-dark mt-1" style="font-size: 12.5px;">${v.name}</div>
                            <div class="text-muted mt-1" style="font-size: 11px;">
                                <i class="bi bi-gift-fill text-warning me-1"></i>${discountText} • ${minOrderText}
                            </div>
                        </div>
                        <button type="button" class="btn btn-sm btn-outline-success fw-bold px-2 py-1" onclick="applyVoucherCode('${v.code}')">
                            Chọn
                        </button>
                    </div>
                `;
            }).join('');
        })
        .catch(() => {
            listContainer.innerHTML = '<div class="text-center text-danger py-3 small">Lỗi kết nối khi tải khuyến mãi</div>';
        });
}

function applyVoucherCode(code) {
    applyVoucherByCode(code, 'modalVoucherMsg');
}

function loadSalesHistory() {
    let dateVal = document.getElementById('historyFilterDate').value;
    if (!dateVal) {
        const today = new Date();
        const yyyy = today.getFullYear();
        let mm = today.getMonth() + 1;
        let dd = today.getDate();
        if (mm < 10) mm = '0' + mm;
        if (dd < 10) dd = '0' + dd;
        dateVal = yyyy + '-' + mm + '-' + dd;
        document.getElementById('historyFilterDate').value = dateVal;
    }

    fetch(`/pos/data/sales-history?date=${dateVal}`)
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById('salesHistoryTableBody');
            tbody.innerHTML = '';

            const isOwner = data.isOwner;
            const orders = data.orders || [];

            const thBranch = document.getElementById('historyBranchCol');
            const totalCols = isOwner ? 7 : 6;
            if (isOwner) {
                if (thBranch) thBranch.style.display = '';
            } else {
                if (thBranch) thBranch.style.display = 'none';
            }

            if (orders.length === 0) {
                tbody.innerHTML = `<tr><td colspan="${totalCols}" class="text-center text-muted py-4">Không có hóa đơn nào được bán trong ngày này.</td></tr>`;
                return;
            }

            orders.forEach(order => {
                let dateStr = "";
                if (order.createdAt) {
                    const dt = new Date(order.createdAt);
                    const hours = String(dt.getHours()).padStart(2, '0');
                    const minutes = String(dt.getMinutes()).padStart(2, '0');
                    const day = String(dt.getDate()).padStart(2, '0');
                    const month = String(dt.getMonth() + 1).padStart(2, '0');
                    dateStr = `${hours}:${minutes} ${day}/${month}`;
                }

                const amountFormatted = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.finalAmount);

                let statusBadge = '';
                if (order.status === 'COMPLETED') {
                    statusBadge = '<span class="badge bg-success">Hoàn thành</span>';
                } else if (order.status === 'CANCELLED') {
                    statusBadge = '<span class="badge bg-danger">Đã hủy</span>';
                } else {
                    statusBadge = `<span class="badge bg-secondary">${order.status}</span>`;
                }

                const tr = document.createElement('tr');

                let branchCell = '';
                if (isOwner) {
                    branchCell = `<td><span class="badge bg-light text-dark border">${order.branchName || 'SMS STORE'}</span></td>`;
                }

                tr.innerHTML = `
                    <td class="fw-semibold text-primary">${order.code}</td>
                    ${branchCell}
                    <td>${dateStr}</td>
                    <td>${order.customerName}</td>
                    <td class="text-end fw-semibold">${amountFormatted}</td>
                    <td class="text-center">${statusBadge}</td>
                    <td class="text-center">
                        <button type="button" class="btn btn-sm btn-outline-primary py-0" style="font-size: 11px;" onclick="showOrderDetail(${order.id})">
                            <i class="bi bi-eye"></i> Chi tiết
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(err => {
            console.error('Error fetching sales history:', err);
            const totalCols = (document.getElementById('historyBranchCol') && document.getElementById('historyBranchCol').style.display !== 'none') ? 7 : 6;
            document.getElementById('salesHistoryTableBody').innerHTML =
                `<tr><td colspan="${totalCols}" class="text-center text-danger py-4">Có lỗi xảy ra khi tải dữ liệu!</td></tr>`;
        });
}

function resetHistoryDate() {
    const today = new Date();
    const yyyy = today.getFullYear();
    let mm = today.getMonth() + 1;
    let dd = today.getDate();
    if (mm < 10) mm = '0' + mm;
    if (dd < 10) dd = '0' + dd;
    document.getElementById('historyFilterDate').value = yyyy + '-' + mm + '-' + dd;
    loadSalesHistory();
}

function showOrderDetail(orderId) {
    const historyModalEl = document.getElementById('salesHistoryModal');
    const historyModal = bootstrap.Modal.getInstance(historyModalEl);
    if (historyModal) {
        historyModal.hide();
    }

    fetch(`/pos/data/order/${orderId}`)
        .then(response => response.json())
        .then(order => {
            let dateStr = "";
            if (order.createdAt) {
                const dt = new Date(order.createdAt);
                const hrs = String(dt.getHours()).padStart(2, '0');
                const mins = String(dt.getMinutes()).padStart(2, '0');
                const day = String(dt.getDate()).padStart(2, '0');
                const month = String(dt.getMonth() + 1).padStart(2, '0');
                const yr = dt.getFullYear();
                dateStr = `${day}/${month}/${yr} ${hrs}:${mins}`;
            }

            let itemsHtml = `
                <table class="w-100 mb-2" style="font-size: 12px; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 1px solid #ddd;">
                            <th class="text-start pb-1">Tên SP</th>
                            <th class="text-center pb-1" style="width: 40px;">SL</th>
                            <th class="text-end pb-1" style="width: 80px;">Đơn giá</th>
                            <th class="text-end pb-1" style="width: 80px;">T.Tiền</th>
                        </tr>
                    </thead>
                    <tbody>
            `;

            order.items.forEach(item => {
                const priceFormatted = new Intl.NumberFormat('vi-VN').format(item.salePrice) + 'đ';
                const subtotalFormatted = new Intl.NumberFormat('vi-VN').format(item.totalAmount) + 'đ';
                itemsHtml += `
                    <tr>
                        <td class="py-1 text-start">${item.productName}</td>
                        <td class="py-1 text-center">${item.quantity}</td>
                        <td class="py-1 text-end">${priceFormatted}</td>
                        <td class="py-1 text-end">${subtotalFormatted}</td>
                    </tr>
                `;
            });

            itemsHtml += `
                    </tbody>
                </table>
            `;

            const totalAmountFormatted = new Intl.NumberFormat('vi-VN').format(order.totalAmount) + 'đ';
            const discountFormatted = '-' + new Intl.NumberFormat('vi-VN').format(order.discountAmount) + 'đ';

            // Calculate VAT: finalAmount + discountAmount - totalAmount
            const vatAmount = Number(order.finalAmount) + Number(order.discountAmount) - Number(order.totalAmount);
            const vatFormatted = new Intl.NumberFormat('vi-VN').format(vatAmount) + 'đ';

            const finalAmountFormatted = new Intl.NumberFormat('vi-VN').format(order.finalAmount) + 'đ';
            const paidFormatted = new Intl.NumberFormat('vi-VN').format(order.paidAmount) + 'đ';
            const changeFormatted = new Intl.NumberFormat('vi-VN').format(order.changeAmount) + 'đ';

            let html = `
                <div class="text-center mb-3">
                    <h5 class="fw-bold mb-1" style="font-family: inherit;">${order.branchName}</h5>
                    <p class="mb-0 text-muted" style="font-size: 11px;">${order.branchAddress}</p>
                    <p class="mb-0 text-muted" style="font-size: 11px;">SĐT: ${order.branchPhone || '0987 654 321'}</p>
                    <div class="my-2" style="border-top: 1px dashed #ccc;"></div>
                    <h6 class="fw-bold my-2" style="font-family: inherit;">HÓA ĐƠN BÁN HÀNG</h6>
                </div>
                <div>
                    <p class="mb-1"><strong>Mã hóa đơn:</strong> <span>${order.code}</span></p>
                    <p class="mb-1"><strong>Ngày tạo:</strong> <span>${dateStr}</span></p>
                    <p class="mb-1"><strong>Khách hàng:</strong> <span>${order.customerName}</span></p>
                    <div class="my-2" style="border-top: 1px dashed #ccc;"></div>
                    
                    ${itemsHtml}
                    
                    <div class="my-2" style="border-top: 1px dashed #ccc;"></div>
                    
                    <div class="d-flex justify-content-between mb-1">
                        <span>Tổng tiền hàng:</span>
                        <span>${totalAmountFormatted}</span>
                    </div>
            `;

            if (Number(order.discountAmount) > 0) {
                html += `
                    <div class="d-flex justify-content-between mb-1 text-danger">
                        <span>Giảm giá:</span>
                        <span>${discountFormatted}</span>
                    </div>
                `;
            }

            html += `
                    <div class="d-flex justify-content-between mb-1">
                        <span>Thuế VAT (2%):</span>
                        <span>${vatFormatted}</span>
                    </div>
                    <div class="d-flex justify-content-between mb-1 fw-bold text-primary" style="font-size: 14px;">
                        <span>KHÁCH PHẢI TRẢ:</span>
                        <span>${finalAmountFormatted}</span>
                    </div>
                    <div class="d-flex justify-content-between mb-1">
                        <span>Tiền khách đưa:</span>
                        <span>${paidFormatted}</span>
                    </div>
                    <div class="d-flex justify-content-between mb-1 text-success fw-bold">
                        <span>Tiền thừa:</span>
                        <span>${changeFormatted}</span>
                    </div>
                    
                    <div class="my-2" style="border-top: 1px dashed #ccc;"></div>
                    <div class="text-center mt-3 small text-muted">
                        <p class="mb-0">Cảm ơn quý khách. Hẹn gặp lại!</p>
                    </div>
                </div>
            `;

            document.getElementById('detailOrderContent').innerHTML = html;

            const detailModalEl = document.getElementById('orderDetailModal');
            let detailModal = bootstrap.Modal.getInstance(detailModalEl);
            if (!detailModal) {
                detailModal = new bootstrap.Modal(detailModalEl);
            }
            detailModal.show();
        })
        .catch(err => {
            console.error('Error fetching order details:', err);
            alert('Không thể tải chi tiết hóa đơn này!');
            if (historyModal) {
                historyModal.show();
            }
        });
}

function backToSalesHistory() {
    const detailModalEl = document.getElementById('orderDetailModal');
    const detailModal = bootstrap.Modal.getInstance(detailModalEl);
    if (detailModal) {
        detailModal.hide();
    }

    const historyModalEl = document.getElementById('salesHistoryModal');
    let historyModal = bootstrap.Modal.getInstance(historyModalEl);
    if (!historyModal) {
        historyModal = new bootstrap.Modal(historyModalEl);
    }
    historyModal.show();
}