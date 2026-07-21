package com.g4fpt.sms.report.service.impl;

import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.report.dto.InventoryMovementProjection;
import com.g4fpt.sms.report.dto.InventoryReportDTO;
import com.g4fpt.sms.report.dto.InventoryReportFilterRequest;
import com.g4fpt.sms.report.repository.InventoryReportRepository;
import com.g4fpt.sms.report.service.InventoryReportService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor // Lombok: tự inject qua constructor cho các field final
public class InventoryReportServiceImpl implements InventoryReportService {

    private final InventoryReportRepository inventoryReportRepository; // chứa sumImportByPeriod, sumExportByPeriod
    private final InventoryRepository inventoryRepository;             // lấy tồn hiện tại
    private final ProductUnitRepository productUnitRepository;         // lấy tên SKU, sản phẩm, ĐVT, nhóm hàng
    private final BranchRepository branchRepository;                   // lấy tên kho

    @Override
    public List<InventoryReportDTO> generateReport(InventoryReportFilterRequest filter) {

        LocalDateTime fromDateTime = filter.getFromDate().atStartOfDay();
        LocalDateTime toDateTime = filter.getToDate().atTime(LocalTime.MAX);
        LocalDateTime now = LocalDateTime.now();

        // 1. Nhập/Xuất TRONG KỲ
        List<InventoryMovementProjection> importInPeriod =
                inventoryReportRepository.sumImportByPeriod(fromDateTime, toDateTime, filter.getBranchId());
        List<InventoryMovementProjection> exportInPeriod =
                inventoryReportRepository.sumExportByPeriod(fromDateTime, toDateTime, filter.getBranchId());

        // 2. Nhập/Xuất TỪ fromDate ĐẾN HIỆN TẠI (dùng để tính lùi ra tồn đầu kỳ)
        List<InventoryMovementProjection> importToNow =
                inventoryReportRepository.sumImportByPeriod(fromDateTime, now, filter.getBranchId());
        List<InventoryMovementProjection> exportToNow =
                inventoryReportRepository.sumExportByPeriod(fromDateTime, now, filter.getBranchId());

        // 3. Tồn hiện tại (bảng Inventory)
        List<Inventory> currentStocks = inventoryRepository.findByFilter(
                filter.getBranchId(), filter.getCategoryId(), filter.getBrandId(), filter.getKeyword());

        // 4. Convert các list projection thành Map để tra nhanh theo key (branchId_productUnitId)
        Map<String, InventoryMovementProjection> importInPeriodMap = toMap(importInPeriod);
        Map<String, InventoryMovementProjection> exportInPeriodMap = toMap(exportInPeriod);
        Map<String, InventoryMovementProjection> importToNowMap = toMap(importToNow);
        Map<String, InventoryMovementProjection> exportToNowMap = toMap(exportToNow);

        // 5. Build từng dòng báo cáo dựa trên danh sách tồn hiện tại (Inventory là nguồn chính)
        List<InventoryReportDTO> result = new ArrayList<>();

        for (Inventory inv : currentStocks) {
            String key = buildKey(inv.getBranchId(), inv.getProductUnitId());

            int tonHienTai = inv.getStock();

            int nhapToNow = getQty(importToNowMap, key);
            int xuatToNow = getQty(exportToNowMap, key);
            BigDecimal giaTriNhapToNow = getValue(importToNowMap, key);
            BigDecimal giaTriXuatToNow = getValue(exportToNowMap, key);

            // Tồn đầu kỳ = tồn hiện tại - nhập(từ fromDate đến now) + xuất(từ fromDate đến now)
            int tonDauKy = tonHienTai - nhapToNow + xuatToNow;
            BigDecimal giaTriDauKy = BigDecimal.valueOf(inv.getStockValue() == null ? 0 : inv.getStockValue())
                    .subtract(giaTriNhapToNow)
                    .add(giaTriXuatToNow); // tuỳ cách bạn lưu giá trị tồn, có thể tính riêng

            int nhapTrongKy = getQty(importInPeriodMap, key);
            int xuatTrongKy = getQty(exportInPeriodMap, key);
            BigDecimal giaTriNhap = getValue(importInPeriodMap, key);
            BigDecimal giaTriXuat = getValue(exportInPeriodMap, key);

            // Tồn cuối kỳ = tồn đầu kỳ + nhập trong kỳ - xuất trong kỳ
            int tonCuoiKy = tonDauKy + nhapTrongKy - xuatTrongKy;
            BigDecimal giaTriCuoiKy = giaTriDauKy.add(giaTriNhap).subtract(giaTriXuat);

            InventoryReportDTO dto = InventoryReportDTO.builder()
                    .branchId(inv.getBranchId())
                    .branchName(inv.getBranch().getName())
                    .productUnitId(inv.getProductUnitId())
                    .sku(inv.getProductUnit().getSku())
                    .productName(inv.getProductUnit().getProduct().getName())
                    .unitName(inv.getProductUnit().getUnit().getName())
                    .categoryName(inv.getProductUnit().getProduct().getCategory().getName())
                    .tonDauKy(tonDauKy)
                    .giaTriDauKy(giaTriDauKy)
                    .nhapTrongKy(nhapTrongKy)
                    .giaTriNhap(giaTriNhap)
                    .xuatTrongKy(xuatTrongKy)
                    .giaTriXuat(giaTriXuat)
                    .tonCuoiKy(tonCuoiKy)
                    .giaTriCuoiKy(giaTriCuoiKy)
                    .build();

            result.add(dto);
        }

        // 6. Nếu không tách theo kho (groupByBranch = false) -> gộp lại theo SKU (bỏ qua branch)
        if (!filter.isGroupByBranch()) {
            result = mergeByProductUnit(result);
        }

        return result;
    }
