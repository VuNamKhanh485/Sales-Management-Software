package com.g4fpt.sms.report.service.impl;

import com.g4fpt.sms.common.exception.ValidationException;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.product.util.ValidationError;
import com.g4fpt.sms.report.emuns.SnapshotType;
import com.g4fpt.sms.report.projection.InventoryMovementProjection;
import com.g4fpt.sms.report.dto.InventoryReportDTO;
import com.g4fpt.sms.report.dto.InventoryReportFilterRequest;
import com.g4fpt.sms.report.projection.LastImportPriceProjection;
import com.g4fpt.sms.report.repository.InventoryReportRepository;
import com.g4fpt.sms.report.service.InventoryReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class InventoryReportServiceImpl implements InventoryReportService {

    private final InventoryReportRepository inventoryReportRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public List<InventoryReportDTO> generateReport(InventoryReportFilterRequest filter) {
        validateFilter(filter);
        LocalDateTime fromDateTime = filter.getFromDate().atStartOfDay();
        LocalDateTime toDateTime = filter.getToDate().atTime(LocalTime.MAX);
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());

        // 1. Nhập/Xuất TRONG KỲ
        Map<String, InventoryMovementProjection> importInPeriodMap =
                toMap(inventoryReportRepository.sumImportByPeriod(fromDateTime, toDateTime, filter.getBranchId()));
        Map<String, InventoryMovementProjection> exportInPeriodMap =
                toMap(inventoryReportRepository.sumExportByPeriod(fromDateTime, toDateTime, filter.getBranchId()));

        // 2. Nhập/Xuất TỪ fromDate ĐẾN HIỆN TẠI (để tính lùi ra tồn đầu kỳ theo SỐ LƯỢNG)
        Map<String, InventoryMovementProjection> importToNowMap =
                toMap(inventoryReportRepository.sumImportByPeriod(fromDateTime, now, filter.getBranchId()));
        Map<String, InventoryMovementProjection> exportToNowMap =
                toMap(inventoryReportRepository.sumExportByPeriod(fromDateTime, now, filter.getBranchId()));

        // 3. Giá nhập gần nhất tính đến ĐẦU kỳ và tính đến CUỐI kỳ (dùng để định giá, KHÔNG lấy từ Inventory)
        Map<Long, BigDecimal> priceAtStart = toPriceMap(
                inventoryReportRepository.findLastImportPrices(fromDateTime, filter.getBranchId()));
        Map<Long, BigDecimal> priceAtEnd = toPriceMap(
                inventoryReportRepository.findLastImportPrices(toDateTime, filter.getBranchId()));

        // 4. Tồn hiện tại (bảng Inventory) - CHỈ lấy số lượng, KHÔNG lấy giá trị từ đây
        List<Inventory> currentStocks = inventoryRepository.findByFilter(
                filter.getBranchId(), filter.getCategoryId(), filter.getBrandId(), filter.getKeyword());

        List<InventoryReportDTO> result = new ArrayList<>();

        for (Inventory inv : currentStocks) {
            String key = buildKey(inv.getBranchId(), inv.getProductUnitId());

            int tonHienTai = inv.getStock();
            int nhapToNow = getQty(importToNowMap, key);
            int xuatToNow = getQty(exportToNowMap, key);

            // Tồn đầu kỳ (số lượng) = tồn hiện tại - nhập(từ fromDate->now) + xuất(từ fromDate->now)
            int tonDauKy = tonHienTai - nhapToNow + xuatToNow;

            int nhapTrongKy = getQty(importInPeriodMap, key);
            int xuatTrongKy = getQty(exportInPeriodMap, key);
            BigDecimal giaTriNhap = getValue(importInPeriodMap, key);
            BigDecimal giaTriXuat = getValue(exportInPeriodMap, key);

            int tonCuoiKy = tonDauKy + nhapTrongKy - xuatTrongKy;

            // Định giá tồn đầu/cuối kỳ bằng giá nhập gần nhất lấy TỪ OrderTransactionDetail
            BigDecimal donGiaDauKy = priceAtStart.getOrDefault(inv.getProductUnitId(), BigDecimal.ZERO);
            BigDecimal donGiaCuoiKy = priceAtEnd.getOrDefault(inv.getProductUnitId(), BigDecimal.ZERO);

            BigDecimal giaTriDauKy = BigDecimal.valueOf(tonDauKy).multiply(donGiaDauKy);
            BigDecimal giaTriCuoiKy = BigDecimal.valueOf(tonCuoiKy).multiply(donGiaCuoiKy);

            InventoryReportDTO dto = InventoryReportDTO.builder()
                    .branchId(inv.getBranchId())
                    .branchName(inv.getBranch().getName())
                    .productUnitId(inv.getProductUnitId())
                    .sku(inv.getProductUnit().getSku())
                    .productName(inv.getProductUnit().getProduct().getName())
                    .unitName(inv.getProductUnit().getUnit().getName())
                    .categoryName(inv.getProductUnit().getProduct().getCategory().getName())
                    .openingStock(tonDauKy)
                    .openingValue(giaTriDauKy)
                    .stockIn(nhapTrongKy)
                    .stockInValue(giaTriNhap)
                    .stockOut(xuatTrongKy)
                    .stockOutValue(giaTriXuat)
                    .closingStock(tonCuoiKy)
                    .closingValue(giaTriCuoiKy)
                    .build();

            result.add(dto);
        }

        if (!filter.isGroupByBranch()) {
            result = mergeByProductUnit(result);
        }

        return result;
    }

    @Override
    public byte[] exportExcel(InventoryReportFilterRequest filter) throws IOException {
        validateFilter(filter);
        List<InventoryReportDTO> data = generateReport(filter);

        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Sheet sheet = workbook.createSheet("Xuat Nhap Ton");

            String[] headers = {"Kho", "Mã SKU", "Tên hàng hóa", "ĐVT", "Nhóm hàng",
                    "Tồn đầu kỳ", "Giá trị", "Nhập trong kỳ", "Giá trị",
                    "Xuất trong kỳ", "Giá trị", "Tồn cuối kỳ", "Giá trị"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) headerRow.createCell(i).setCellValue(headers[i]);

            int rowIdx = 1;
            for (InventoryReportDTO dto : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getBranchName() != null ? dto.getBranchName() : "Tất cả kho");
                row.createCell(1).setCellValue(dto.getSku());
                row.createCell(2).setCellValue(dto.getProductName());
                row.createCell(3).setCellValue(dto.getUnitName());
                row.createCell(4).setCellValue(dto.getCategoryName());
                row.createCell(5).setCellValue(dto.getOpeningStock());
                row.createCell(6).setCellValue(dto.getOpeningValue().doubleValue());
                row.createCell(7).setCellValue(dto.getStockIn());
                row.createCell(8).setCellValue(dto.getStockInValue().doubleValue());
                row.createCell(9).setCellValue(dto.getStockOut());
                row.createCell(10).setCellValue(dto.getStockOutValue().doubleValue());
                row.createCell(11).setCellValue(dto.getClosingStock());
                row.createCell(12).setCellValue(dto.getClosingValue().doubleValue());
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ===== Helper =====
    private void validateFilter(InventoryReportFilterRequest filter) {
        List<ValidationError> errors = new ArrayList<>();

        if (filter.getFromDate() == null || filter.getToDate() == null) {
            errors.add(new ValidationError("Error", "Vui lòng chọn đầy đủ Từ ngày và Đến ngày"));
        } else {
            if (filter.getFromDate().isAfter(filter.getToDate())) {
                errors.add(new ValidationError("Error", "Từ ngày không được sau Đến ngày"));
            }

            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            if (filter.getToDate().isAfter(today)) {
                errors.add(new ValidationError("Error", "Đến ngày không được lớn hơn ngày hiện tại"));
            }

            long monthsBetween = ChronoUnit.MONTHS.between(
                    filter.getFromDate().withDayOfMonth(1),
                    filter.getToDate().withDayOfMonth(1));
            if (monthsBetween > 24) {
                errors.add(new ValidationError("Error", "Khoảng thời gian báo cáo không được vượt quá 24 tháng"));
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private String buildKey(Long branchId, Long productUnitId) {
        return branchId + "_" + productUnitId;
    }

    private Map<String, InventoryMovementProjection> toMap(List<InventoryMovementProjection> list) {
        return list.stream().collect(Collectors.toMap(
                p -> buildKey(p.getBranchId(), p.getProductUnitId()),
                p -> p,
                (a, b) -> a
        ));
    }

    private Map<Long, BigDecimal> toPriceMap(List<LastImportPriceProjection> list) {
        return list.stream().collect(Collectors.toMap(
                LastImportPriceProjection::getProductUnitId,
                p -> p.getImportPrice() == null ? BigDecimal.ZERO : p.getImportPrice(),
                (a, b) -> a
        ));
    }

    private int getQty(Map<String, InventoryMovementProjection> map, String key) {
        InventoryMovementProjection p = map.get(key);
        return p == null || p.getQty() == null ? 0 : p.getQty();
    }

    private BigDecimal getValue(Map<String, InventoryMovementProjection> map, String key) {
        InventoryMovementProjection p = map.get(key);
        return p == null || p.getValue() == null ? BigDecimal.ZERO : p.getValue();
    }

    private List<InventoryReportDTO> mergeByProductUnit(List<InventoryReportDTO> data) {
        Map<Long, InventoryReportDTO> merged = new LinkedHashMap<>();
        for (InventoryReportDTO dto : data) {
            merged.merge(dto.getProductUnitId(), dto, (existing, incoming) -> {
                existing.setOpeningStock(existing.getOpeningStock() + incoming.getOpeningStock());
                existing.setOpeningValue(existing.getOpeningValue().add(incoming.getOpeningValue()));
                existing.setStockIn(existing.getStockIn() + incoming.getStockIn());
                existing.setStockInValue(existing.getStockInValue().add(incoming.getStockInValue()));
                existing.setStockOut(existing.getStockOut() + incoming.getStockOut());
                existing.setStockOutValue(existing.getStockOutValue().add(incoming.getStockOutValue()));
                existing.setClosingStock(existing.getClosingStock() + incoming.getClosingStock());
                existing.setClosingValue(existing.getClosingValue().add(incoming.getClosingValue()));
                existing.setBranchName(null);
                return existing;
            });
        }
        return new ArrayList<>(merged.values());
    }

    private SnapshotType resolveSnapshotType(LocalDate fromDate) {
        Month month = fromDate.getMonth();
        if (fromDate.getDayOfMonth() == 1) {
            if (month == Month.JANUARY) return SnapshotType.YEAR;
            if (month == Month.APRIL || month == Month.JULY || month == Month.OCTOBER) return SnapshotType.QUARTER;
            return SnapshotType.MONTH;
        }
        return SnapshotType.MONTH; // fallback
    }
}
