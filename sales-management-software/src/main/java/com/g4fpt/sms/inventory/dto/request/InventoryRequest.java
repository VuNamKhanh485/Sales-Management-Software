package com.g4fpt.sms.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequest {

    @NotNull(message = "Chi nhánh không được để trống")
    private Long branchId;

    @NotNull(message = "Đơn vị sản phẩm không được để trống")
    private Long productUnitId;

    @NotNull(message = "Số lượng tồn không được để trống")
    @Min(value = 0, message = "Số lượng tồn phải >= 0")
    private Integer stock;

    @NotNull(message = "Tồn kho tối thiểu không được để trống")
    @Min(value = 0, message = "Tồn kho tối thiểu phải >= 0")
    private Integer minStock;

    @Min(value = 0, message = "Tồn kho tối đa phải >= 0")
    private Integer maxStock;

    private String positionInShop;
}