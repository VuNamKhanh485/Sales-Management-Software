

    /*
     * mở modal để tạo danh mục, nhãn hàng, đơn vị
     * ở ngay trong trang tạo sản phẩm
     * @param id
     * @param button
     */
    function openModal(id, button = null) {
        if (id === "modalUnit" && button) {
            currentUnitSelect = $(button)
                .closest("td")
                .find("select");
        }

        document.getElementById(id).style.display = "flex";
    }

    let currentPreviewUrl = null;

    function previewImage(input) {
        const file = input.files[0];
        const img = document.getElementById('imagePreview');
        const display = document.getElementById('previewFrame');

        if (currentPreviewUrl) {
            URL.revokeObjectURL(currentPreviewUrl);
            currentPreviewUrl = null;
        }

        if (file) {
            currentPreviewUrl = URL.createObjectURL(file);
            img.src = currentPreviewUrl;
            display.style.display = 'block';
        } else {
            display.style.display = 'none';
        }
    }

    function updateBarcodePreview(row){
        const preview = row.querySelector(".barcode-preview-box");
        const barcodeInput = row.querySelector(".barcode-input");
        const barcodeValue = barcodeInput.value.trim();

        if(barcodeValue === ""){
            preview.style.display = "none";
        }else {
            preview.style.display = "";
            const svg = row.querySelector(".barcode-svg");
            const text = row.querySelector(".barcode-text");

            if (barcodeValue === "") {
                svg.innerHTML = "";
                text.innerHTML = "";
                return;
            }
            JsBarcode(svg, barcodeValue, {
                format: "CODE128",
                width: 2,
                height: 50,
                displayValue: false
            });
            text.innerText = barcodeValue;
        }
    }

    /*
     * hiển thị ảnh barcode
     * @param row
     */
    function initBarcode(row) {
        const sku = row.querySelector(".sku-input");
        const barcode = row.querySelector(".barcode-input");

        if (!sku || !barcode) return;
        let edited = false;
        barcode.addEventListener("input", function () {
            edited = true;
            updateBarcodePreview(row);
        });
        sku.addEventListener("input", function () {
            if (!edited) {
                barcode.value = sku.value;
            }
            updateBarcodePreview(row);
        });

        updateBarcodePreview(row);
    }

    /*
     * Thêm dòng đơn vị khi bấm thêm đơn vị tính,
     * append vào table
     * update barcode preview
     * tạo select cho phần đơn vị để phục vụ việc tạo mới đơn vị tính ngay
     */
    function addUnitRow() {
        const noUnitRow = document.getElementById('no-unit-row');
        if (noUnitRow) noUnitRow.style.display = 'none';

        const tbody = document.getElementById('unitTable');
        const i = rowIndex++;

        let options = '<option value="">-- Chọn --</option>';
        // Add 'NEW_UNIT' option to existing unit list
        unitList.forEach(u => {
            options += `<option value="${u.id}">${u.name}</option>`;
        });
        const tr = document.createElement('tr');

        tr.innerHTML = `
        <td class="row-index">${i + 1}</td>

        <td>
            <div class="select-with-action">
                <select class="form-select unit-select2"
                        name="productUnitsRequest[${i}].unitId"
                        style="width:120px;">
                    ${options}
                </select>

                <button type="button"
                        class="btn-link"
                        onclick="openModal('modalUnit',this)">
                    + Thêm
                </button>
            </div>
        </td>

        <td>
            <input type="number"
                   class="form-input"
                   name="productUnitsRequest[${i}].conventionValue"
                   value="1"
                   style="padding:4px 8px;font-size:13px;width:70px;">

        </td>

        <td>
            <input type="number"
                   class="form-input"
                   name="productUnitsRequest[${i}].price"
                   style="padding:4px 8px;font-size:13px;width:100px;">

        </td>

        <td>
            <input type="text"
                   class="form-input sku-input"
                   name="productUnitsRequest[${i}].sku"
                   style="padding:4px 8px;font-size:13px;width:100px;">

        </td>

        <td>
            <input type="text"
                   class="form-input barcode-input"
                   name="productUnitsRequest[${i}].barcodeUnit"
                   style="padding:4px 8px;font-size:13px;width:100px;">

            <div class="barcode-preview-box">
                <svg class="barcode-svg"></svg>
                <div class="barcode-text"></div>
            </div>
        </td>

        <td style="text-align:center;">
            <input type="checkbox"
                   name="productUnitsRequest[${i}].isBaseUnit">
        </td>

        <td>
            <button type="button"
                    class="btn-icon"
                    style="width:24px;height:24px;font-size:12px;color:var(--danger);background:transparent;"
                    onclick="removeUnitRow(this)">
                ✕
            </button>
        </td>
    `;

        tbody.appendChild(tr);

        initBarcode(tr);
        updateBarcodePreview(tr);

        // Khởi tạo Select2 cho select vừa thêm
        $(tr).find(".unit-select2").select2();

        reindexRows();
    }

    function removeUnitRow(btn) {
        btn.closest('tr').remove();
        reindexRows();

        const tbody = document.getElementById('unitTable');
        if (tbody.querySelectorAll('tr').length === 1 && document.getElementById('no-unit-row')) {
            document.getElementById('no-unit-row').style.display = '';
        }
    }

    function reindexRows() {
        const rows = document.querySelectorAll('#unitTable tr:not(#no-unit-row)');
        rows.forEach((row, index) => {
            row.querySelector('.row-index').innerText = index + 1;
            
            // Re-index inputs
            const selects = row.querySelectorAll('select');
            selects.forEach(s => s.name = s.name.replace(/\[\d+\]/, '[' + index + ']'));
            
            const inputs = row.querySelectorAll('input');
            inputs.forEach(i => i.name = i.name.replace(/\[\d+\]/, '[' + index + ']'));
        });
        rowIndex = rows.length;
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

    window.addEventListener('message', function(event) {
        const data = event.data;
        if (!data || !data.type) return;

        if (data.type === 'CATEGORY_CREATED') {
            const option = new Option(data.name, data.id, true, true);
            $('#categoryId').append(option).trigger('change');
            closeModal('modalCategory');
        } else if (data.type === 'BRAND_CREATED') {
            const option = new Option(data.name, data.id, true, true);
            $('#brandId').append(option).trigger('change');
            closeModal('modalBrand');
        } else if (data.type === 'UNIT_CREATED') {
            unitList.push({id: data.id, name: data.name});
            $('select[name^="productUnitsRequest"], .unit-select2').each(function() {
                const opt = new Option(data.name, data.id, false, false);
                $(this).append(opt);
            });
            if (currentUnitSelect) {
                $(currentUnitSelect).val(data.id).trigger('change');
            }
            closeModal('modalUnit');
        }
    });

    function filterSuppliers() {
        const searchVal = document.getElementById('supplierSearch').value.toLowerCase();
        const items = document.querySelectorAll('.supplier-item');
        items.forEach(item => {
            const text = item.textContent.toLowerCase();
            if (text.includes(searchVal)) {
                item.style.display = 'flex';
            } else {
                item.style.display = 'none';
            }
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        $('.select2-init').select2();
        $('.select2-no-search').select2({
            minimumResultsForSearch: Infinity
        });
        $('.unit-select2').select2({
            minimumResultsForSearch: Infinity
        });
        document.querySelectorAll("#unitTable tr:not(#no-unit-row)")
            .forEach(function (row) {
                initBarcode(row);
            });
    });



