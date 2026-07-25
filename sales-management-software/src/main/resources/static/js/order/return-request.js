let currentUrls = [];

function previewImages(input) {
    const container = document.getElementById('previewContainer');
    container.innerHTML = '';
    
    // Hủy các đường dẫn tạm cũ để giải phóng RAM
    currentUrls.forEach(url => URL.revokeObjectURL(url));
    currentUrls = [];

    if (input.files && input.files.length > 0) {
        container.style.display = 'flex';
        Array.from(input.files).forEach(file => {
            const url = URL.createObjectURL(file);
            currentUrls.push(url);

            const div = document.createElement('div');
            div.style.position = 'relative';
            div.style.width = '100px';
            div.style.height = '100px';
            
            const img = document.createElement('img');
            img.src = url;
            img.className = 'w-100 h-100 object-fit-cover rounded border';
            
            div.appendChild(img);
            container.appendChild(div);
        });
    } else {
        container.style.display = 'none';
    }
}
document.addEventListener('DOMContentLoaded', function() {
    const checkboxes = document.querySelectorAll('.item-cb');
    checkboxes.forEach(cb => {
        const toggleInput = () => {
            const qtyInput = cb.closest('.item-check').querySelector('.qty-input');
            if (qtyInput) {
                qtyInput.disabled = !cb.checked;
            }
        };
        cb.addEventListener('change', toggleInput);
        toggleInput(); // Run on init
    });
});
