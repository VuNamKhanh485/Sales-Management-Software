    function toggleUnits(productId) {
        const row = document.getElementById('units-' + productId);
        const btn = document.getElementById('btn-' + productId);
        if (row.style.display === 'none') {
            row.style.display = '';
            btn.classList.add('expanded');
        } else {
            row.style.display = 'none';
            btn.classList.remove('expanded');
        }
    }

    function closeModal(id) {
        const modal = document.getElementById(id);
        modal.style.display = "none";

        const iframe = modal.querySelector("iframe");
        if (iframe) {
            iframe.contentWindow.location.reload();
        }
        if(id==="modalUnit"){
            currentUnitSelect=null;
        }
    }

    const barcodeModal = document.getElementById("barcodeModal");

    barcodeModal.addEventListener("click", function (e) {
        if (e.target === this) {
            closeModal("barcodeModal");
        }
    });

    function showBarcode(button){
        const row = button.closest("tr");
        const barcode = row
            .querySelector(".barcode-value")
            .innerText
            .trim();

        JsBarcode(
            "#barcodeSvg",
            barcode,
            {
                format:"CODE128",
                width:1,
                height:45,
                displayValue:false
            }
        );
        document
            .getElementById("barcodeText")
            .innerText = barcode;
        document
            .getElementById("barcodeModal")
            .style.display="flex";

    }

    function changeSize(newSize) {
        const form = document.getElementById('filterForm');
        form.querySelector('[name=size]').value = newSize;
        form.querySelector('[name=page]').value = 0;
        form.submit();
    }

        setTimeout(() => {
        document.querySelectorAll('.page-content > div[style*="border-left"]')
            .forEach(el => el.remove());
    }, 4000);

