package com.g4fpt.sms.report.service.impl;

import com.g4fpt.sms.report.emuns.SnapshotType;
import com.g4fpt.sms.report.repository.InventorySnapshotRepository;
import com.g4fpt.sms.report.service.InventorySnapshotService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

@Service
@AllArgsConstructor
public class InventorySnapshotServiceImpl implements InventorySnapshotService {
    private final InventorySnapshotRepository snapshotRepository;

    @Override
    @Transactional
    public void createSnapshot(LocalDate today, SnapshotType type) {

        LocalDate snapshotDate;

        switch (type) {
            case MONTH:
                if (today.getDayOfMonth() != 1) {
                    return;
                }
                snapshotDate = today.
                        minusMonths(1).
                        with(TemporalAdjusters.lastDayOfMonth());
                break;
            case QUARTER:
                if (today.getDayOfMonth() != 1) {
                    return;
                }
                Month month = today.getMonth();
                if (month != Month.APRIL
                        && month != Month.JULY
                        && month != Month.OCTOBER
                        && month != Month.JANUARY) {
                    return;
                }
                snapshotDate = today.
                        minusMonths(1).
                        with(TemporalAdjusters.lastDayOfMonth());
                break;

            case YEAR:

                if (today.getDayOfMonth() != 1
                        || today.getMonth() != Month.JANUARY) {
                    return;
                }

                snapshotDate = LocalDate
                        .of(today.getYear() - 1, 12, 31);

                break;

            default:
                throw new IllegalArgumentException("Unsupported snapshot type");
        }

        if (snapshotRepository.existsBySnapshotTypeAndSnapshotDate(type, snapshotDate)) {
            return;
        }
    }
}
