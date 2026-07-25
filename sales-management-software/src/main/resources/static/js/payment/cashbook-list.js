function exportExcel() {
    var branchId = document.querySelector('select[name="branchId"]').value;
    var type = document.querySelector('select[name="type"]').value;
    var method = document.querySelector('select[name="method"]').value;
    
    var url = '/cashbook/export?';
    if (branchId) url += 'branchId=' + branchId + '&';
    if (type) url += 'type=' + type + '&';
    if (method) url += 'method=' + method + '&';
    
    window.location.href = url;
}
