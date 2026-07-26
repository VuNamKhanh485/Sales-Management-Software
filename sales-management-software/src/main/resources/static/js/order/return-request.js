// Mảng lưu các file hiện tại
let selectedFiles = [];

function previewImages(input) {
    // Lấy các file mới được chọn và gộp với file cũ
    if (input.files && input.files.length > 0) {
        Array.from(input.files).forEach(file => {
            selectedFiles.push(file);
        });
    }
    renderPreviews(input);
}

function renderPreviews(input) {
    const container = document.getElementById('previewContainer');
    container.innerHTML = '';

    if (selectedFiles.length === 0) {
        container.style.display = 'none';
        return;
    }

    container.style.display = 'flex';

    selectedFiles.forEach((file, index) => {
        const url = URL.createObjectURL(file);

        const div = document.createElement('div');
        div.style.cssText = 'position:relative; width:100px; height:100px; flex-shrink:0;';

        const img = document.createElement('img');
        img.src = url;
        img.style.cssText = 'width:100%; height:100%; object-fit:cover; border-radius:8px; border:1px solid #ddd;';

        // Nút xóa
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.innerHTML = '&times;';
        btn.style.cssText = `
            position:absolute; top:-6px; right:-6px;
            width:22px; height:22px; border-radius:50%;
            background:#ef4444; color:#fff; border:none;
            font-size:14px; line-height:1; cursor:pointer;
            display:flex; align-items:center; justify-content:center;
            box-shadow:0 1px 4px rgba(0,0,0,0.3);
        `;
        btn.onclick = function() {
            URL.revokeObjectURL(url);
            selectedFiles.splice(index, 1);

            // Cập nhật lại input file để submit đúng
            const dt = new DataTransfer();
            selectedFiles.forEach(f => dt.items.add(f));
            input.files = dt.files;

            renderPreviews(input);
        };

        div.appendChild(img);
        div.appendChild(btn);
        container.appendChild(div);
    });

    // Đồng bộ input
    const dt = new DataTransfer();
    selectedFiles.forEach(f => dt.items.add(f));
    input.files = dt.files;
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


