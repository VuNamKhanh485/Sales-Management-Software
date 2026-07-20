document.addEventListener('DOMContentLoaded', function() {
    const detailModalEl = document.getElementById('detailModal');
    if (detailModalEl) {
        var modal = new bootstrap.Modal(detailModalEl);
        modal.show();
    }
});
