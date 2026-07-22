package com.g4fpt.sms.report.service.impl;

import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class InventoryReportServiceImpl implements InventoryReportService {

    private final InventoryReportRepository inventoryReportRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public List<InventoryReportDTO> generateReport(InventoryReportFilterRequest filter) {

        LocalDateTime fromDateTime = filter.getFromDate().atStartOfDay();
        LocalDateTime toDateTime = filter.getToDate().atTime(LocalTime.MAX);
        LocalDateTime now = LocalDateTime.now();

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

        if (!filter.isGroupByBranch()) {
            result = mergeByProductUnit(result);
        }

        return result;
    }

    @Override
    public void exportExcel(InventoryReportFilterRequest filter, HttpServletResponse response) throws IOException {
        List<InventoryReportDTO> data = generateReport(filter);

        try (Workbook workbook = new XSSFWorkbook()) {
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
                row.createCell(5).setCellValue(dto.getTonDauKy());
                row.createCell(6).setCellValue(dto.getGiaTriDauKy().doubleValue());
                row.createCell(7).setCellValue(dto.getNhapTrongKy());
                row.createCell(8).setCellValue(dto.getGiaTriNhap().doubleValue());
                row.createCell(9).setCellValue(dto.getXuatTrongKy());
                row.createCell(10).setCellValue(dto.getGiaTriXuat().doubleValue());
                row.createCell(11).setCellValue(dto.getTonCuoiKy());
                row.createCell(12).setCellValue(dto.getGiaTriCuoiKy().doubleValue());
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=bao_cao_xuat_nhap_ton_" + LocalDate.now() + ".xlsx");
            workbook.write(response.getOutputStream());
        }
    }

    // ===== Helper =====

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
                existing.setTonDauKy(existing.getTonDauKy() + incoming.getTonDauKy());
                existing.setGiaTriDauKy(existing.getGiaTriDauKy().add(incoming.getGiaTriDauKy()));
                existing.setNhapTrongKy(existing.getNhapTrongKy() + incoming.getNhapTrongKy());
                existing.setGiaTriNhap(existing.getGiaTriNhap().add(incoming.getGiaTriNhap()));
                existing.setXuatTrongKy(existing.getXuatTrongKy() + incoming.getXuatTrongKy());
                existing.setGiaTriXuat(existing.getGiaTriXuat().add(incoming.getGiaTriXuat()));
                existing.setTonCuoiKy(existing.getTonCuoiKy() + incoming.getTonCuoiKy());
                existing.setGiaTriCuoiKy(existing.getGiaTriCuoiKy().add(incoming.getGiaTriCuoiKy()));
                existing.setBranchName(null);
                return existing;
            });
        }
        return new ArrayList<>(merged.values());
    }
}
