// =============================================
// Biến cấu hình — được tải từ DOM (#pos-config)
// =============================================
let finalAmount = 0;
let customerId = null;
let showSuccessModal = false;
let successOrderCode = '';

function loadConfig() {
    const configEl = document.getElementById('pos-config');
    if (configEl) {
        finalAmount = parseFloat(configEl.getAttribute('data-final-amount')) || 0;
        customerId = configEl.getAttribute('data-customer-id') || null;
        showSuccessModal = configEl.getAttribute('data-show-success-modal') === 'true';
        successOrderCode = configEl.getAttribute('data-success-order-code') || '';
    }
}

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
// 4. Modal khách hàng mới và sự kiện CUSTOMER_CREATED
// =============================================
function closeCustomerModal() {
    const modalEl = document.getElementById('newCustomerModal');
    if (modalEl) {
        const modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
        modal.hide();
    }
}

window.addEventListener('message', function(event) {
    const data = event.data;
    if (!data || !data.type) return;

    if (data.type === 'CUSTOMER_CREATED') {
        closeCustomerModal();
        const cust = data.data; // Customer object
        // Reload với set-customer
        window.location.href = `/pos/set-customer?customerId=${cust.id}&customerName=${encodeURIComponent(cust.fullName)}&customerPhone=${encodeURIComponent(cust.phone)}`;
    }
});

// =============================================
// Khởi tạo khi load trang
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

// =============================================
// AJAX / PJAX dynamic loading (No reload)
// =============================================
function updateDomWithHtml(html) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(html, 'text/html');

    // 1. Replace the cart table wrapper
    const newCartTable = doc.querySelector('.cart-table-wrapper');
    const currentCartTable = document.querySelector('.cart-table-wrapper');
    if (newCartTable && currentCartTable) {
        currentCartTable.innerHTML = newCartTable.innerHTML;
    }

    // 2. Replace the right panel (checkout panel)
    const newRightPanel = doc.querySelector('.pos-right');
    const currentRightPanel = document.querySelector('.pos-right');
    if (newRightPanel && currentRightPanel) {
        currentRightPanel.innerHTML = newRightPanel.innerHTML;
    }

    // 3. Replace config element to update finalAmount etc.
    const newConfig = doc.getElementById('pos-config');
    const currentConfig = document.getElementById('pos-config');
    if (newConfig && currentConfig) {
        currentConfig.setAttribute('data-final-amount', newConfig.getAttribute('data-final-amount'));
        currentConfig.setAttribute('data-customer-id', newConfig.getAttribute('data-customer-id'));
    }

    // 4. Update the order tabs row
    const newTabs = doc.querySelector('.order-tabs-row');
    const currentTabs = document.querySelector('.order-tabs-row');
    if (newTabs && currentTabs) {
        currentTabs.innerHTML = newTabs.innerHTML;
    }

    // 5. Replace alerts
    const newLeftAlerts = doc.querySelector('.pos-left .alert');
    const currentLeft = document.querySelector('.pos-left');
    if (currentLeft) {
        const existingAlerts = currentLeft.querySelectorAll('.alert');
        existingAlerts.forEach(el => el.remove());
        if (newLeftAlerts) {
            currentLeft.insertBefore(newLeftAlerts, currentLeft.firstChild);
        }
    }

    // Re-initialize dynamic JS logic
    loadConfig();
    generateSuggestions();
    calcChange();
}

function reloadPosFragments(url) {
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(html => {
            updateDomWithHtml(html);
        })
        .catch(error => {
            console.error('Error reloading POS fragments:', error);
            window.location.reload();
        });
}

function updateQty(index, qty, event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }
    reloadPosFragments(`/pos/update-qty?index=${index}&quantity=${qty}`);
}

function updateUnit(index, productUnitId) {
    reloadPosFragments(`/pos/update-unit?index=${index}&productUnitId=${productUnitId}`);
}

function removeCartItem(index, event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }
    reloadPosFragments(`/pos/remove?index=${index}`);
}

function selectCustomer(customerId, name, phone, event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }
    reloadPosFragments(`/pos/set-customer?customerId=${customerId}&customerName=${encodeURIComponent(name)}&customerPhone=${encodeURIComponent(phone)}`);
}

function removeCustomer(event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }
    reloadPosFragments('/pos/remove-customer');
}

function searchCustomer(event) {
    event.preventDefault();
    const phone = document.getElementById('customerPhone').value;
    reloadPosFragments(`/pos/search-customer?phone=${encodeURIComponent(phone)}`);
}

function removeVoucher(event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }
    reloadPosFragments('/pos/remove-voucher');
}

function applyVoucher(event) {
    event.preventDefault();
    const code = document.getElementById('voucherCode').value;
    const formData = new URLSearchParams();
    formData.append('code', code);
    
    fetch('/pos/apply-voucher', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: formData.toString()
    })
    .then(response => response.text())
    .then(html => {
        updateDomWithHtml(html);
    })
    .catch(error => {
        console.error('Error applying voucher:', error);
        window.location.reload();
    });
}

function addBySku(event) {
    event.preventDefault();
    const input = document.getElementById('skuKeyword');
    const keyword = input.value.trim();
    if (!keyword) return;
    input.value = ''; // Clear input immediately
    reloadPosFragments(`/pos/add?keyword=${encodeURIComponent(keyword)}`);
}

function addById(productUnitId) {
    reloadPosFragments(`/pos/add-by-id?productUnitId=${productUnitId}`);
    
    const productModalEl = document.getElementById('productModal');
    if (productModalEl) {
        const modal = bootstrap.Modal.getInstance(productModalEl);
        if (modal) {
            modal.hide();
        }
    }
}

function switchOrder(index, event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }
    reloadPosFragments(`/pos/switch/${index}`);
}

function changeBranch(branchId, event) {
    if (event) {
        event.preventDefault();
    }
    window.location.href = `/pos/change-branch?branchId=${branchId}`;
}

// Reload iframe contents when showing Modals to refresh stock/data
document.addEventListener('DOMContentLoaded', () => {
    const productModalEl = document.getElementById('productModal');
    if (productModalEl) {
        productModalEl.addEventListener('show.bs.modal', () => {
            const iframe = productModalEl.querySelector('iframe');
            if (iframe) {
                iframe.src = iframe.src;
            }
        });
    }
    const voucherModalEl = document.getElementById('voucherModal');
    if (voucherModalEl) {
        voucherModalEl.addEventListener('show.bs.modal', () => {
            const iframe = voucherModalEl.querySelector('iframe');
            if (iframe) {
                iframe.src = iframe.src;
            }
        });
    }
});