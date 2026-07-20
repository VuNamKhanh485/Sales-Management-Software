// supplier-form.js
document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById('supplierForm');
    const nameInput = document.getElementById('supplierName');
    const phoneInput = document.getElementById('supplierPhone');
    const submitBtn = document.getElementById('btnSubmit');

    if (form) {
        form.addEventListener('submit', function(e) {
            // Validate số điện thoại (chỉ chứa số, độ dài 10-11 ký tự)
            if (phoneInput && phoneInput.value.trim() !== '') {
                const phoneRegex = /^[0-9]{10,11}$/;
                if (!phoneRegex.test(phoneInput.value.trim())) {
                    alert('Số điện thoại không hợp lệ! Vui lòng nhập 10-11 số.');
                    phoneInput.focus();
                    e.preventDefault();
                    return;
                }
            }

            // Đổi text nút submit khi đang xử lý để tránh double click
            if (submitBtn) {
                submitBtn.textContent = 'Đang lưu...';
                submitBtn.disabled = true;
                submitBtn.style.opacity = '0.7';
                
                // Allow form submission normally
                form.submit();
            }
        });
    }

    // Tự động viết hoa chữ cái đầu cho tên
    if (nameInput) {
        nameInput.addEventListener('blur', function() {
            let val = this.value.trim();
            if(val) {
                this.value = val.charAt(0).toUpperCase() + val.slice(1);
            }
        });
    }
});
