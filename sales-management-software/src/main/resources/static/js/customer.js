document.addEventListener("DOMContentLoaded", function() {
    // Tìm tất cả các thẻ có class là 'alert' (thông báo thành công/lỗi)
    const alerts = document.querySelectorAll('.alert');

    alerts.forEach(function(alert) {
        // Sau 3 giây (3000ms) sẽ tự động làm mờ và ẩn đi
        setTimeout(function() {
            alert.style.transition = "opacity 0.5s ease"; // Hiệu ứng mờ dần
            alert.style.opacity = "0";

            // Đợi mờ hẳn (500ms) rồi mới xóa khỏi màn hình
            setTimeout(() => alert.style.display = 'none', 500);
        }, 3000);
    });
});