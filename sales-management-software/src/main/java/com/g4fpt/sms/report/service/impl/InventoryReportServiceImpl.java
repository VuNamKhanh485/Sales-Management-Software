package com.g4fpt.sms.report.service.impl;

import com.g4fpt.sms.common.exception.ValidationException;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.product.util.ValidationError;
import com.g4fpt.sms.report.emuns.SnapshotType;
import com.g4fpt.sms.report.entity.InventorySnapshot;
import com.g4fpt.sms.report.projection.InventoryMovementProjection;
import com.g4fpt.sms.report.dto.InventoryReportDTO;
import com.g4fpt.sms.report.dto.InventoryReportFilterRequest;
import com.g4fpt.sms.report.projection.LastImportPriceProjection;
import com.g4fpt.sms.report.repository.InventoryReportRepository;
import com.g4fpt.sms.report.repository.InventorySnapshotRepository;
import com.g4fpt.sms.report.service.InventoryReportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.g4fpt.sms.report.emuns.SnapshotType.*;


@Service
@RequiredArgsConstructor
public class InventoryReportServiceImpl implements InventoryReportService {

    private final InventoryReportRepository inventoryReportRepository;
    private final InventoryRepository inventoryRepository;
    private final InventorySnapshotRepository  inventorySnapshotRepository;

    private static final LocalDateTime EPOCH = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final int PRICE_SCALE = 4;
    
    public List<InventoryReportDTO> generateReport(InventoryReportFilterRequest filter) {
        return switch (filter.getSnapshotType()) {
            case DAY -> generateDayReport(filter);
            case MONTH -> generateMonthReport(filter);
            case QUARTER -> generateQuarterReport(filter);
            case YEAR -> generateYearReport(filter);
        };
    }

    public List<InventoryReportDTO> generateDayReport(InventoryReportFilterRequest filter) {
        validateFilter(filter);

        LocalDateTime fromDateTime = filter.getFromDate().atStartOfDay();
        LocalDateTime toDateTime = filter.getToDate().atTime(LocalTime.MAX);
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());

        // ================== Giao dịch TRONG KỲ báo cáo ==================
        Map<String, InventoryMovementProjection> importInPeriod =
                toMap(inventoryReportRepository.sumImportByPeriod(fromDateTime, toDateTime, filter.getBranchId()));
        Map<String, InventoryMovementProjection> exportInPeriod =
                toMap(inventoryReportRepository.sumExportByPeriod(fromDateTime, toDateTime, filter.getBranchId()));

        // ================== Giao dịch từ fromDate -> hiện tại (để suy ra SL tồn đầu kỳ) ==================
        Map<String, InventoryMovementProjection> importToNow =
                toMap(inventoryReportRepository.sumImportByPeriod(fromDateTime, now, filter.getBranchId()));
        Map<String, InventoryMovementProjection> exportToNow =
                toMap(inventoryReportRepository.sumExportByPeriod(fromDateTime, now, filter.getBranchId()));

        // ================== Tồn hiện tại ==================
        List<Inventory> currentStocks = inventoryRepository.findByFilter(
                filter.getBranchId(), filter.getCategoryId(), filter.getBrandId(), filter.getKeyword());

        // ================== Snapshot gần nhất trước fromDate (nếu có) ==================
        SnapshotType snapshotType = resolveSnapshotType(filter.getFromDate());
        List<InventorySnapshot> snapshots =
                inventorySnapshotRepository.findLatestSnapshots(snapshotType, filter.getFromDate(), filter.getBranchId());

        Map<String, InventorySnapshot> snapshotMap = snapshots.stream()
                .collect(Collectors.toMap(s -> buildKey(s.getBranchId(), s.getProductUnitId()), s -> s));

        LocalDate snapshotDate = snapshots.isEmpty() ? null : snapshots.get(0).getSnapshotDate();

        Map<String, InventoryMovementProjection> importAfterSnapshot = Collections.emptyMap();
        Map<String, InventoryMovementProjection> exportAfterSnapshot = Collections.emptyMap();

        if (snapshotDate != null && snapshotDate.isBefore(filter.getFromDate())) {
            LocalDateTime snapshotTime = snapshotDate.atStartOfDay();
            importAfterSnapshot = toMap(inventoryReportRepository.sumImportByPeriod(
                    snapshotTime, fromDateTime.minusNanos(1), filter.getBranchId()));
            exportAfterSnapshot = toMap(inventoryReportRepository.sumExportByPeriod(
                    snapshotTime, fromDateTime.minusNanos(1), filter.getBranchId()));
        }

        // ================== Nhập lũy kế từ đầu lịch sử -> fromDate (dùng khi KHÔNG có snapshot) ==================
        Map<String, InventoryMovementProjection> importCumulativeToFromDate =
                toMap(inventoryReportRepository.sumImportByPeriod(EPOCH, fromDateTime.minusNanos(1), filter.getBranchId()));

        List<InventoryReportDTO> result = new ArrayList<>();

        for (Inventory inv : currentStocks) {
            String key = buildKey(inv.getBranchId(), inv.getProductUnitId());

            int openingStock;
            BigDecimal openingValue;

            InventorySnapshot snapshot = snapshotMap.get(key);

            if (snapshot != null) {
                // ----- Có snapshot: tính tiếp phần phát sinh giữa snapshot và fromDate theo bình quân gia quyền -----
                int gapOpeningStock = snapshot.getClosingStock();
                BigDecimal gapOpeningValue = snapshot.getClosingValue();

                int gapImportQty = getQty(importAfterSnapshot, key);
                BigDecimal gapImportValue = getValue(importAfterSnapshot, key);

                BigDecimal gapAvgPrice = calculateAveragePrice(
                        gapOpeningValue.add(gapImportValue), gapOpeningStock + gapImportQty);

                int gapExportQty = getQty(exportAfterSnapshot, key);
                BigDecimal gapExportValue = gapAvgPrice.multiply(BigDecimal.valueOf(gapExportQty));

                openingStock = gapOpeningStock + gapImportQty - gapExportQty;
                openingValue = gapOpeningValue.add(gapImportValue).subtract(gapExportValue);

            } else {
                // ----- Không có snapshot: xấp xỉ bằng đơn giá bình quân nhập lũy kế đến fromDate -----
                int currentStock = inv.getStock();
                openingStock = currentStock - getQty(importToNow, key) + getQty(exportToNow, key);

                int cumQty = getQty(importCumulativeToFromDate, key);
                BigDecimal cumValue = getValue(importCumulativeToFromDate, key);
                BigDecimal avgPriceAtFromDate = calculateAveragePrice(cumValue, cumQty);

                openingValue = avgPriceAtFromDate.multiply(BigDecimal.valueOf(openingStock));
            }

            // ================== Tính Nhập/Xuất/Tồn cuối kỳ theo bình quân gia quyền ==================
            int stockIn = getQty(importInPeriod, key);
            BigDecimal stockInValue = getValue(importInPeriod, key); // giá nhập thực tế, lấy nguyên từ OrderTransactionDetail

            BigDecimal avgPriceThisPeriod = calculateAveragePrice(
                    openingValue.add(stockInValue), openingStock + stockIn);

            int stockOut = getQty(exportInPeriod, key);
            BigDecimal stockOutValue = avgPriceThisPeriod.multiply(BigDecimal.valueOf(stockOut)); // giá vốn xuất, KHÔNG lấy sale_price

            int closingStock = openingStock + stockIn - stockOut;
            BigDecimal closingValue = openingValue.add(stockInValue).subtract(stockOutValue);

            result.add(InventoryReportDTO.builder()
                    .branchId(inv.getBranchId())
                    .branchName(inv.getBranch().getName())
                    .productUnitId(inv.getProductUnitId())
                    .sku(inv.getProductUnit().getSku())
                    .productName(inv.getProductUnit().getProduct().getName())
                    .unitName(inv.getProductUnit().getUnit().getName())
                    .categoryName(inv.getProductUnit().getProduct().getCategory().getName())
                    .openingStock(openingStock)
                    .openingValue(openingValue)
                    .stockIn(stockIn)
                    .stockInValue(stockInValue)
                    .stockOut(stockOut)
                    .stockOutValue(stockOutValue)
                    .closingStock(closingStock)
                    .closingValue(closingValue)
                    .build());
        }

        if (!filter.isGroupByBranch()) {
            result = mergeByProductUnit(result);
        }

        return result;
    }

    private List<InventoryReportDTO> fromSnapshots(List<InventorySnapshot> snapshots,
                                                   boolean groupByBranch) {

        List<InventoryReportDTO> result = snapshots.stream()
                .map(s -> InventoryReportDTO.builder()
                        .branchId(s.getBranchId())
                        .branchName(s.getBranch().getName())
                        .productUnitId(s.getProductUnitId())
                        .sku(s.getProductUnit().getSku())
                        .productName(s.getProductUnit().getProduct().getName())
                        .unitName(s.getProductUnit().getUnit().getName())
                        .categoryName(s.getProductUnit().getProduct().getCategory().getName())
                        .openingStock(s.getOpeningStock())
                        .openingValue(s.getOpeningValue())
                        .stockIn(s.getStockIn())
                        .stockInValue(s.getStockInValue())
                        .stockOut(s.getStockOut())
                        .stockOutValue(s.getStockOutValue())
                        .closingStock(s.getClosingStock())
                        .closingValue(s.getClosingValue())
                        .build())
                .toList();

        if (!groupByBranch) {
            result = mergeByProductUnit(result);
        }

        return result;
    }
    public List<InventoryReportDTO> generateMonthReport(
            InventoryReportFilterRequest filter) {

        LocalDate snapshotDate =
                filter.getFromDate().withDayOfMonth(1);

        List<InventorySnapshot> snapshots =
                inventorySnapshotRepository.findSnapshotReport(
                        SnapshotType.MONTH,
                        snapshotDate,
                        filter.getBranchId(),
                        filter.getCategoryId(),
                        filter.getBrandId(),
                        filter.getKeyword());

        return fromSnapshots(snapshots, filter.isGroupByBranch());
    }

    public List<InventoryReportDTO> generateQuarterReport(
            InventoryReportFilterRequest filter) {

        int quarter = (filter.getFromDate().getMonthValue() - 1) / 3 + 1;

        LocalDate snapshotDate = switch (quarter) {
            case 1 -> LocalDate.of(filter.getFromDate().getYear(), 1, 1);
            case 2 -> LocalDate.of(filter.getFromDate().getYear(), 4, 1);
            case 3 -> LocalDate.of(filter.getFromDate().getYear(), 7, 1);
            default -> LocalDate.of(filter.getFromDate().getYear(), 10, 1);
        };

        List<InventorySnapshot> snapshots =
                inventorySnapshotRepository.findSnapshotReport(
                        SnapshotType.QUARTER,
                        snapshotDate,
                        filter.getBranchId(),
                        filter.getCategoryId(),
                        filter.getBrandId(),
                        filter.getKeyword());

        return fromSnapshots(snapshots, filter.isGroupByBranch());
    }

    public List<InventoryReportDTO> generateYearReport(
            InventoryReportFilterRequest filter) {

        LocalDate snapshotDate =
                LocalDate.of(filter.getFromDate().getYear(), 1, 1);

        List<InventorySnapshot> snapshots =
                inventorySnapshotRepository.findSnapshotReport(
                        SnapshotType.YEAR,
                        snapshotDate,
                        filter.getBranchId(),
                        filter.getCategoryId(),
                        filter.getBrandId(),
                        filter.getKeyword());

        return fromSnapshots(snapshots, filter.isGroupByBranch());
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


    /**
     * Tính đơn giá bình quân = giá trị / số lượng.
     * Trả về 0 nếu số lượng <= 0 (tránh chia cho 0).
     */
    private BigDecimal calculateAveragePrice(BigDecimal totalValue, int totalQty) {
        if (totalQty <= 0) return BigDecimal.ZERO;
        return totalValue.divide(BigDecimal.valueOf(totalQty), PRICE_SCALE, RoundingMode.HALF_UP);
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
            if (month == Month.APRIL || month == Month.JULY || month == Month.OCTOBER) return QUARTER;
            return MONTH;
        }
        return MONTH; // fallback
    }
}
