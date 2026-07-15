document.addEventListener("DOMContentLoaded", function() {
    // Tự động ẩn thông báo sau 3 giây
    const alerts = document.querySelectorAll('.alert');

    alerts.forEach(function(alert) {
        setTimeout(function() {
            alert.style.transition = "opacity 0.5s ease";
            alert.style.opacity = "0";

            setTimeout(() => alert.style.display = 'none', 500);
        }, 3000);
    });
});