function closeGlobalConfirmModal() {
    const modalEl = document.getElementById('confirmModal');
    if (modalEl) modalEl.style.display = 'none';
}

// Hàm hiển thị Modal Xác Nhận Chung
function showConfirmModal(title, message, onConfirm) {
    const modalEl = document.getElementById('confirmModal');
    if (!modalEl) {
        console.warn("confirmModal not found in DOM");
        return;
    }
    
    document.getElementById('confirmModalTitle').innerHTML = title || 'Xác nhận';
    document.getElementById('confirmModalMessage').textContent = message || 'Bạn có chắc chắn muốn thực hiện hành động này?';
    
    const confirmBtn = document.getElementById('confirmModalBtn');
    
    // Gỡ bỏ sự kiện click cũ bằng cách clone node
    const newConfirmBtn = confirmBtn.cloneNode(true);
    confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);
    
    newConfirmBtn.addEventListener('click', function() {
        closeGlobalConfirmModal();
        if (typeof onConfirm === 'function') {
            onConfirm();
        }
    });
    
    modalEl.style.display = 'flex';
}

// Khởi tạo các link có attribute data-confirm
function initConfirmActions() {
    // Khởi tạo các link (thẻ a)
    document.querySelectorAll('.confirm-action, a[data-confirm]').forEach(el => {
        // Tránh gán sự kiện nhiều lần nếu đã gán rồi
        if (el.dataset.hasConfirmAction) return;
        el.dataset.hasConfirmAction = "true";

        el.addEventListener('click', function(e) {
            e.preventDefault();
            const message = this.getAttribute('data-confirm');
            const title = this.getAttribute('data-confirm-title') || 'Xác nhận';
            const href = this.getAttribute('href');
            
            showConfirmModal(title, message, () => {
                if (href && href !== '#') {
                    window.location.href = href;
                }
            });
        });
    });

    // Cho các form (nút submit)
    document.querySelectorAll('.confirm-action-form, form[data-confirm]').forEach(form => {
        if (form.dataset.hasConfirmAction) return;
        form.dataset.hasConfirmAction = "true";

        form.addEventListener('submit', function(e) {
            if (this.dataset.confirmed !== "true") {
                e.preventDefault();
                const msg = this.getAttribute('data-confirm');
                const title = this.getAttribute('data-confirm-title') || 'Xác nhận';
                const formEl = this;
                showConfirmModal(title, msg, function() {
                    formEl.dataset.confirmed = "true";
                    formEl.submit();
                });
            }
        });
    });
}

document.addEventListener('DOMContentLoaded', () => {
    initConfirmActions();
});
