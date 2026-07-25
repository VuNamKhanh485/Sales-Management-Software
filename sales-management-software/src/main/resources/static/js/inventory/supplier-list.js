// supplier-list.js
document.addEventListener("DOMContentLoaded", function() {
    // Xử lý xác nhận khi bấm nút xóa (mặc định đã dùng onsubmit trong HTML nhưng tách riêng ra đây cũng được)
    const deleteForms = document.querySelectorAll('.delete-form');
    deleteForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            // Tạm thời nếu trong HTML có onsubmit return confirm rồi thì ko cần đoạn này.
            // Đoạn script này dự phòng hoặc override
            if(!form.hasAttribute('onsubmit')) {
                const isConfirmed = confirm('Bạn có chắc chắn muốn xóa nhà cung cấp này? Thao tác này không thể hoàn tác.');
                if (!isConfirmed) {
                    e.preventDefault();
                }
            }
        });
    });

    // Tự động ẩn thông báo sau 3 giây
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 3000);
    });
});
