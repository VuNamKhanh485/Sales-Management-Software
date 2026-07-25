document.addEventListener('DOMContentLoaded', () => {
    // Initialize Toast (Bootstrap 5)
    var toastElList = [].slice.call(document.querySelectorAll('.toast'));
    var toastList = toastElList.map(function(toastEl) {
        return new bootstrap.Toast(toastEl, { delay: 3000 });
    });
    toastList.forEach(toast => toast.show());

    const form = document.getElementById('profileForm');


    // Client-side validation before submit
    if (form) {
        form.addEventListener('submit', (e) => {
            let isValid = true;

            // Clear previous JS errors
            document.querySelectorAll('.js-error').forEach(el => el.textContent = '');
            document.querySelectorAll('.form-control').forEach(f => f.classList.remove('is-invalid'));
            
            // Hide server errors to make way for client errors if any
            document.querySelectorAll('.d-block.invalid-feedback').forEach(el => el.style.display = 'none');

            // Validate Full Name
            const fullName = document.getElementById('fullName');
            if (fullName && !fullName.value.trim()) {
                showError(fullName, 'Họ và tên không được để trống.');
                isValid = false;
            }



            // Validate Phone (10 digits)
            const phone = document.getElementById('phone');
            const phoneRegex = /^[0-9]{10}$/;
            if (phone && !phoneRegex.test(phone.value.trim())) {
                showError(phone, 'Số điện thoại phải gồm đúng 10 chữ số.');
                isValid = false;
            }

            // Validate DOB (not greater than today)
            const dob = document.getElementById('dob');
            if (dob && dob.value) {
                const dobDate = new Date(dob.value);
                const today = new Date();
                today.setHours(0,0,0,0);
                if (dobDate > today) {
                    showError(dob, 'Ngày sinh không được lớn hơn ngày hiện tại.');
                    isValid = false;
                }
            }

            // Validate Address
            const address = document.getElementById('address');
            if (address && !address.value.trim()) {
                showError(address, 'Địa chỉ không được để trống.');
                isValid = false;
            }

            if (!isValid) {
                e.preventDefault(); // Stop submission if invalid
            }
        });
    }

    function showError(element, message) {
        element.classList.add('is-invalid');
        const errorDiv = document.getElementById(element.id + 'Error');
        if (errorDiv) {
            errorDiv.textContent = message;
        }
    }
});
