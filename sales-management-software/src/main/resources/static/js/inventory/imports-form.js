document.addEventListener('DOMContentLoaded', function() {
    var activeProducts = window.activeProducts || [];

    // Elements
    var quickProductSearch = document.getElementById('quick-product-search');
    var quickProductId = document.getElementById('quick-product-id');
    var quickProductDropdown = document.getElementById('quick-product-dropdown');
    var quickUnitSelect = document.getElementById('quick-unit-select');
    var quickQuantity = document.getElementById('quick-quantity');
    var quickPrice = document.getElementById('quick-price');
    var quickAddBtn = document.getElementById('quick-add-btn');
    var tableBody = document.getElementById('item-table-body');
    var emptyTableRow = document.getElementById('empty-table-row');
    var importForm = document.getElementById('import-form');

    // Expose global function for inline row events
    window.updateRowTotal = function(input) {
        var row = input.closest('tr');
        var qty = parseInt(row.querySelector('.quantity-input').value) || 0;
        var price = parseFloat(row.querySelector('.price-input').value) || 0;
        row.querySelector('.row-total').textContent = formatCurrency(qty * price);
        updateGrandTotal();
    };

    window.removeRow = function(button) {
        if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi danh sách nhập hàng?')) {
            var row = button.closest('tr');
            row.remove();

            var rows = tableBody.querySelectorAll('tr.item-row');
            if (rows.length === 0) {
                if (emptyTableRow) {
                    emptyTableRow.style.display = 'table-row';
                }
            }

            reindexRows();
            updateGrandTotal();
        }
    };

    // Mở dropdown tìm kiếm sản phẩm khi focus
    if(quickProductSearch) {
        quickProductSearch.addEventListener('focus', function() {
            showProductDropdown();
        });

        quickProductSearch.addEventListener('input', function() {
            filterProducts();
        });
    }

    // Close dropdown when clicking outside
    document.addEventListener('click', function(e) {
        if (quickProductSearch && quickProductDropdown && e.target !== quickProductSearch && !quickProductDropdown.contains(e.target)) {
            quickProductDropdown.style.display = 'none';
        }
    });

    function showProductDropdown() {
        quickProductDropdown.style.display = 'block';
        filterProducts();
    }

    function filterProducts() {
        var query = quickProductSearch.value.trim().toLowerCase();
        quickProductDropdown.innerHTML = '';

        if (!activeProducts || activeProducts.length === 0) {
            quickProductDropdown.innerHTML = '<div class="dropdown-item" style="color: var(--ink-muted-48); cursor: default;">Không có sản phẩm nào hoạt động</div>';
            return;
        }

        var matches = activeProducts.filter(function(product) {
            if (product.name && product.name.toLowerCase().includes(query)) return true;
            if (product.productUnitsResponses) {
                for (var i = 0; i < product.productUnitsResponses.length; i++) {
                    var pu = product.productUnitsResponses[i];
                    if (pu.sku && pu.sku.toLowerCase().includes(query)) return true;
                    if (pu.barcodeUnit && pu.barcodeUnit.toLowerCase().includes(query)) return true;
                }
            }
            return false;
        });

        if (matches.length === 0) {
            quickProductDropdown.innerHTML = '<div class="dropdown-item" style="color: var(--ink-muted-48); cursor: default;">Không tìm thấy sản phẩm nào</div>';
            return;
        }

        matches.forEach(function(product) {
            var skus = [];
            var barcodes = [];
            if (product.productUnitsResponses) {
                product.productUnitsResponses.forEach(function(pu) {
                    if (pu.sku) skus.push(pu.sku);
                    if (pu.barcodeUnit) barcodes.push(pu.barcodeUnit);
                });
            }

            var itemBtn = document.createElement('div');
            itemBtn.className = 'dropdown-item';
            
            var html = '<strong>' + escapeHtml(product.name) + '</strong>';
            if (skus.length > 0 || barcodes.length > 0) {
                html += '<div style="font-size: 12px; color: var(--ink-muted-48); margin-top: 4px;">';
                if (skus.length > 0) html += 'SKU: ' + escapeHtml(skus.join(', ')) + ' ';
                if (barcodes.length > 0) html += '| Barcode: ' + escapeHtml(barcodes.join(', '));
                html += '</div>';
            }
            itemBtn.innerHTML = html;

            itemBtn.addEventListener('mousedown', function(e) {
                e.preventDefault(); // Tránh blur gây đóng dropdown trước khi chọn
                selectProduct(product);
            });

            quickProductDropdown.appendChild(itemBtn);
        });
    }

    function selectProduct(product) {
        quickProductSearch.value = product.name;
        quickProductId.value = product.id;
        quickProductDropdown.style.display = 'none';

        // Đổ danh sách đơn vị quy đổi
        quickUnitSelect.innerHTML = '<option value="">-- Đơn vị --</option>';
        quickUnitSelect.disabled = false;

        if (product.productUnitsResponses) {
            product.productUnitsResponses.forEach(function(pu) {
                var opt = document.createElement('option');
                opt.value = pu.id;
                opt.textContent = pu.unit.name + ' (Giá bán: ' + pu.price.toLocaleString('vi-VN') + ' đ)';
                opt.dataset.sku = pu.sku;
                opt.dataset.unitName = pu.unit.name;
                opt.dataset.productName = product.name;
                quickUnitSelect.appendChild(opt);
            });
        }

        // Tự động focus sang ô Đơn vị
        quickUnitSelect.focus();
    }

    // Khi chọn đơn vị, tự động focus sang Số lượng
    if(quickUnitSelect) {
        quickUnitSelect.addEventListener('change', function() {
            if (quickUnitSelect.value) {
                quickQuantity.focus();
                quickQuantity.select();
            }
        });
    }

    if(quickQuantity) {
        quickQuantity.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                quickPrice.focus();
                quickPrice.select();
            }
        });
    }

    if(quickPrice) {
        quickPrice.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                addProductToTable();
            }
        });
    }

    if(quickAddBtn) {
        quickAddBtn.addEventListener('click', function() {
            addProductToTable();
        });
    }

    function addProductToTable() {
        var productUnitId = quickUnitSelect.value;
        if (!quickProductId.value || !productUnitId) {
            alert('Vui lòng chọn sản phẩm và đơn vị hợp lệ!');
            return;
        }

        var qty = parseInt(quickQuantity.value) || 0;
        var price = parseFloat(quickPrice.value) || 0;

        if (qty <= 0) {
            alert('Số lượng nhập phải lớn hơn 0!');
            quickQuantity.focus();
            return;
        }
        if (price < 0) {
            alert('Giá nhập không được âm!');
            quickPrice.focus();
            return;
        }

        var selectedOpt = quickUnitSelect.options[quickUnitSelect.selectedIndex];
        var productName = selectedOpt.dataset.productName;
        var unitName = selectedOpt.dataset.unitName;
        var sku = selectedOpt.dataset.sku || '';

        // Kiểm tra xem trùng lặp sản phẩm & đơn vị đã có trong bảng chưa
        var existingRow = findExistingRow(productUnitId);
        if (existingRow) {
            // Gộp số lượng
            var qtyInput = existingRow.querySelector('.quantity-input');
            var currentQty = parseInt(qtyInput.value) || 0;
            qtyInput.value = currentQty + qty;
            
            // Cập nhật giá nhập mới nếu người dùng nhập giá nhập mới khác 0
            if (price > 0) {
                existingRow.querySelector('.price-input').value = price;
            }
            
            window.updateRowTotal(qtyInput);
        } else {
            // Thêm hàng mới
            if (emptyTableRow) {
                emptyTableRow.style.display = 'none';
            }

            var newRow = document.createElement('tr');
            newRow.className = 'item-row';
            newRow.dataset.productUnitId = productUnitId;

            newRow.innerHTML = 
                '<td class="stt-cell text-center" style="color: var(--ink-muted-48); font-size: 13px;"></td>' +
                '<td>' +
                    '<span style="font-weight: 500;">' + escapeHtml(productName) + '</span>' +
                    '<input type="hidden" name="items[0].productUnitId" class="product-unit-id-field" value="' + productUnitId + '" />' +
                '</td>' +
                '<td style="color: var(--ink-muted-48); font-size: 13px;">' + escapeHtml(sku) + '</td>' +
                '<td><span class="badge badge-inactive">' + escapeHtml(unitName) + '</span></td>' +
                '<td>' +
                    '<input type="number" class="form-input quantity-input table-input-borderless text-center" style="width: 80px; padding: 4px;" name="items[0].quantity" value="' + qty + '" min="1" oninput="updateRowTotal(this)" required />' +
                '</td>' +
                '<td>' +
                    '<input type="number" class="form-input price-input table-input-borderless text-right" style="width: 120px; padding: 4px;" name="items[0].importPrice" value="' + price + '" min="0" step="1000" oninput="updateRowTotal(this)" required />' +
                '</td>' +
                '<td class="row-total value-cash text-right">' + formatCurrency(qty * price) + '</td>' +
                '<td class="text-center">' +
                    '<button type="button" class="btn btn-secondary btn-secondary--sm" onclick="removeRow(this)">Xóa</button>' +
                '</td>';

            tableBody.appendChild(newRow);
        }

        // Đánh lại chỉ mục và tính tổng
        reindexRows();
        updateGrandTotal();

        // Clear thanh nhập nhanh sản phẩm và focus lại ô Tìm sản phẩm
        quickProductSearch.value = '';
        quickProductId.value = '';
        quickUnitSelect.innerHTML = '<option value="">-- Đơn vị --</option>';
        quickUnitSelect.disabled = true;
        quickQuantity.value = 1;
        quickPrice.value = '';
        
        quickProductSearch.focus();
    }

    function findExistingRow(productUnitId) {
        var rows = tableBody.querySelectorAll('tr.item-row');
        for (var i = 0; i < rows.length; i++) {
            var unitIdField = rows[i].querySelector('.product-unit-id-field');
            if (unitIdField && unitIdField.value === productUnitId) {
                return rows[i];
            }
        }
        return null;
    }

    function reindexRows() {
        var rows = tableBody.querySelectorAll('tr.item-row');
        rows.forEach(function(row, index) {
            row.querySelectorAll('input').forEach(function(field) {
                var name = field.getAttribute('name');
                if (name) {
                    field.setAttribute('name', name.replace(/items\[\d+\]/, 'items[' + index + ']'));
                }
            });
            // Số thứ tự
            var sttCol = row.querySelector('.stt-cell');
            if (sttCol) {
                sttCol.textContent = index + 1;
            }
        });
    }

    function updateGrandTotal() {
        var total = 0;
        var totalQty = 0;
        var rows = tableBody.querySelectorAll('tr.item-row');

        rows.forEach(function(row) {
            var qty = parseInt(row.querySelector('.quantity-input').value) || 0;
            var price = parseFloat(row.querySelector('.price-input').value) || 0;
            total += qty * price;
            totalQty += qty;
        });

        // Cập nhật thông số tóm tắt
        var skusElem = document.getElementById('summary-skus');
        if(skusElem) skusElem.textContent = rows.length;

        var qtyElem = document.getElementById('summary-qty');
        if(qtyElem) qtyElem.textContent = totalQty;

        var totalElem = document.getElementById('grand-total');
        if(totalElem) totalElem.textContent = formatCurrency(total);
    }

    function formatCurrency(amount) {
        return amount.toLocaleString('vi-VN') + ' đ';
    }

    function escapeHtml(text) {
        if (!text) return '';
        return text
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    // Validation submit
    if(importForm) {
        importForm.addEventListener('submit', function(e) {
            var rows = tableBody.querySelectorAll('tr.item-row');
            if (rows.length === 0) {
                e.preventDefault();
                alert('Vui lòng thêm ít nhất một sản phẩm vào danh sách trước khi gửi yêu cầu nhập hàng!');
                quickProductSearch.focus();
            }
        });
    }
});
