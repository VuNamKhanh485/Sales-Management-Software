document.addEventListener("DOMContentLoaded", function () {
    const discountTypeSelect = document.getElementById("discountType");
    const discountValueInput = document.getElementById("discountValue");
    const maxDiscountGroup = document.getElementById("maxDiscountGroup");
    const maxDiscountInput = document.getElementById("maxDiscountAmount");

    function syncDiscountType() {
        const type = discountTypeSelect.value;
        if (type === "PERCENT") {
            maxDiscountGroup.classList.remove("form-group--hidden");
        } else if (type === "AMOUNT") {
            maxDiscountGroup.classList.add("form-group--hidden");
            maxDiscountInput.value = discountValueInput.value;
        } else {
            maxDiscountGroup.classList.add("form-group--hidden");
        }
    }

    discountTypeSelect.addEventListener("change", syncDiscountType);
    discountValueInput.addEventListener("input", function () {
        if (discountTypeSelect.value === "AMOUNT") {
            maxDiscountInput.value = discountValueInput.value;
        }
    });

    // Date validation: endAt >= startAt
    const startAt = document.getElementById("startAt");
    const endAt = document.getElementById("endAt");

    // isEdit is passed from Thymeleaf global window variable
    if (!window.isEdit) {
        const now = new Date();
        const tzoffset = now.getTimezoneOffset() * 60000;
        const localISOTime = (new Date(now - tzoffset)).toISOString().slice(0, 16);

        startAt.min = localISOTime;
        if (!startAt.value) {
            startAt.value = localISOTime;
        }
        endAt.min = localISOTime;
    }

    startAt.addEventListener("change", function () {
        if (endAt.value && endAt.value < startAt.value) {
            endAt.value = startAt.value;
        }
        endAt.min = startAt.value;
    });

    syncDiscountType();
});
