package com.g4fpt.sms.report.repository;

import com.g4fpt.sms.report.emuns.SnapshotType;
import com.g4fpt.sms.report.entity.InventorySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventorySnapshotRepository
        extends JpaRepository<InventorySnapshot, Long> {

    boolean existsBySnapshotTypeAndSnapshotDate(
            SnapshotType snapshotType,
            LocalDate snapshotDate
    );

    Optional<InventorySnapshot> findTopBySnapshotTypeAndSnapshotDateLessThanEqualOrderBySnapshotDateDesc(
            SnapshotType type,
            LocalDate snapshotDate
    );

    List<InventorySnapshot> findBySnapshotTypeAndSnapshotDate(
            SnapshotType type,
            LocalDate snapshotDate
    );

}
