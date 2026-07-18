document.addEventListener('DOMContentLoaded', () => {
    // Initialize Toast (Bootstrap 5)
    var toastElList = [].slice.call(document.querySelectorAll('.toast'));
    var toastList = toastElList.map(function(toastEl) {
        return new bootstrap.Toast(toastEl, { delay: 3000 });
    });
    toastList.forEach(toast => toast.show());

    // Toggle Password Visibility
    const togglePasswordBtns = document.querySelectorAll('.toggle-password');
    togglePasswordBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target');
            const inputElement = document.getElementById(targetId);
            const icon = this.querySelector('i');
            
            if (inputElement.type === 'password') {
                inputElement.type = 'text';
                icon.classList.remove('bi-eye');
                icon.classList.add('bi-eye-slash');
            } else {
                inputElement.type = 'password';
                icon.classList.remove('bi-eye-slash');
                icon.classList.add('bi-eye');
            }
        });
    });

    // Password Form Validation
    const passwordForm = document.getElementById('passwordForm');
    if (passwordForm) {
        passwordForm.addEventListener('submit', (e) => {
            let isValid = true;
            
            // Clear previous errors
            document.querySelectorAll('.js-pwd-error').forEach(el => el.textContent = '');
            document.querySelectorAll('#passwordForm .form-control').forEach(f => f.classList.remove('is-invalid'));
            document.querySelectorAll('#passwordForm .invalid-feedback.d-block').forEach(el => el.style.display = 'none');

            const currentPassword = document.getElementById('currentPassword');
            const newPassword = document.getElementById('newPassword');
            const confirmPassword = document.getElementById('confirmPassword');

            if (!currentPassword.value.trim()) {
                showError('currentPassword', 'currentPasswordError', 'Vui lòng nhập mật khẩu hiện tại.');
                isValid = false;
            }

            if (!newPassword.value.trim()) {
                showError('newPassword', 'newPasswordError', 'Vui lòng nhập mật khẩu mới.');
                isValid = false;
            } else {
                // Password complexity check
                const pwdRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
                if (!pwdRegex.test(newPassword.value)) {
                    showError('newPassword', 'newPasswordError', 'Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.');
                    isValid = false;
                } else if (newPassword.value === currentPassword.value) {
                    showError('newPassword', 'newPasswordError', 'Mật khẩu mới không được trùng với mật khẩu cũ.');
                    isValid = false;
                }
            }

            if (!confirmPassword.value.trim()) {
                showError('confirmPassword', 'confirmPasswordError', 'Vui lòng xác nhận mật khẩu mới.');
                isValid = false;
            } else if (newPassword.value !== confirmPassword.value) {
                showError('confirmPassword', 'confirmPasswordError', 'Xác nhận mật khẩu không khớp.');
                isValid = false;
            }

            if (!isValid) {
                e.preventDefault();
            }
        });
    }

    function showError(inputId, errorId, message) {
        const input = document.getElementById(inputId);
        const error = document.getElementById(errorId);
        if (input && error) {
            input.classList.add('is-invalid');
            error.textContent = message;
        }
    }
});
