package com.g4fpt.sms.report.service;

import com.g4fpt.sms.report.emuns.SnapshotType;

import java.time.LocalDate;

public interface InventorySnapshotService {
    void createSnapshot(LocalDate date, SnapshotType type);

}
