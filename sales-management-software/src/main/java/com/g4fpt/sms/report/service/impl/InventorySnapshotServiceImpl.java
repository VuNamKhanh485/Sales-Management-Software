package com.g4fpt.sms.report.service.impl;

import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.report.emuns.SnapshotType;
import com.g4fpt.sms.report.entity.InventorySnapshot;
import com.g4fpt.sms.report.projection.LastImportPriceProjection;
import com.g4fpt.sms.report.repository.InventoryReportRepository;
import com.g4fpt.sms.report.repository.InventorySnapshotRepository;
import com.g4fpt.sms.report.service.InventorySnapshotService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InventorySnapshotServiceImpl implements InventorySnapshotService {
    private final InventorySnapshotRepository snapshotRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryReportRepository inventoryReportRepository;

    @Override
    @Transactional
    public void createSnapshot(LocalDate today, SnapshotType type) {
        LocalDate snapshotDate;

        switch (type) {
            case MONTH:
                if (today.getDayOfMonth() != 1) return;
                snapshotDate = today.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                break;
            case QUARTER:
                if (today.getDayOfMonth() != 1) return;
                Month month = today.getMonth();
                if (month != Month.APRIL && month != Month.JULY &&
                        month != Month.OCTOBER && month != Month.JANUARY) return;
                snapshotDate = today.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                break;
            case YEAR:
                if (today.getDayOfMonth() != 1 || today.getMonth() != Month.JANUARY) return;
                snapshotDate = LocalDate.of(today.getYear() - 1, 12, 31);
                break;
            default:
                throw new IllegalArgumentException("Unsupported snapshot type");
        }

        // Nếu snapshot đã tồn tại thì bỏ qua
        if (snapshotRepository.existsBySnapshotTypeAndSnapshotDate(type, snapshotDate)) {
            return;
        }

        // 1. Lấy tồn kho hiện tại
        List<Inventory> inventories = inventoryRepository.findAll();

        // 2. Lấy giá nhập gần nhất tính đến snapshotDate
        List<LastImportPriceProjection> lastPrices =
                inventoryReportRepository.findLastImportPrices(snapshotDate.atTime(LocalTime.MAX), null);
        Map<Long, BigDecimal> priceMap = lastPrices.stream().collect(Collectors.toMap(
                LastImportPriceProjection::getProductUnitId,
                p -> p.getImportPrice() == null ? BigDecimal.ZERO : p.getImportPrice(),
                (a, b) -> a
        ));

        // 3. Tạo danh sách snapshot
        List<InventorySnapshot> snapshots = inventories.stream().map(inv -> {
            BigDecimal lastImportPrice = priceMap.getOrDefault(inv.getProductUnitId(), BigDecimal.ZERO);
            BigDecimal value = BigDecimal.valueOf(inv.getStock()).multiply(lastImportPrice);

            return InventorySnapshot.builder()
                    .snapshotType(type)
                    .snapshotDate(snapshotDate)
                    .branchId(inv.getBranchId())
                    .productUnitId(inv.getProductUnitId())
                    .openingStock(inv.getStock())
                    .openingValue(value)
                    .stockIn(0)
                    .stockInValue(BigDecimal.ZERO)
                    .stockOut(0)
                    .stockOutValue(BigDecimal.ZERO)
                    .closingStock(inv.getStock())
                    .closingValue(value)
                    .build();
        }).collect(Collectors.toList());

        // 4. Lưu snapshot
        snapshotRepository.saveAll(snapshots);
    }
}
