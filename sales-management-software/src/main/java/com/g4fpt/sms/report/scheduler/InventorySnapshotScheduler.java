package com.g4fpt.sms.report.scheduler;

import com.g4fpt.sms.report.emuns.SnapshotType;
import com.g4fpt.sms.report.service.InventorySnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class InventorySnapshotScheduler {

    private final InventorySnapshotService inventorySnapshotService;

    /**
     * Chạy lúc 00:10 mỗi ngày.
     */
    @Scheduled(cron = "0 10 0 * * *")
    public void createSnapshot() {

        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        inventorySnapshotService.createSnapshot(today, SnapshotType.MONTH);
        inventorySnapshotService.createSnapshot(today, SnapshotType.QUARTER);
        inventorySnapshotService.createSnapshot(today,  SnapshotType.YEAR);
    }

}
