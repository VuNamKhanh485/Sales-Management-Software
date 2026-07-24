package com.g4fpt.sms.payment.service.impl;

import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.payment.dto.CashbookDTO;
import com.g4fpt.sms.payment.entity.CashbookTransaction;
import com.g4fpt.sms.payment.repository.CashbookTransactionRepository;
import com.g4fpt.sms.payment.service.CashbookService;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashbookServiceImpl implements CashbookService {

    private final CashbookTransactionRepository cashbookTransactionRepository;
    private final BranchRepository branchRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Page<CashbookTransaction> getTransactions(Long branchId, String type, String method,
            LocalDateTime startDate, LocalDateTime endDate,
            int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        boolean hasBranch = branchId != null;
        boolean hasType = type != null && !type.isEmpty();
        boolean hasMethod = method != null && !method.isEmpty();

        if (hasBranch && hasType && hasMethod)
            return cashbookTransactionRepository.findByBranchIdAndTransactionTypeAndPaymentMethod(branchId, type,
                    method, pageable);
        if (hasBranch && hasType)
            return cashbookTransactionRepository.findByBranchIdAndTransactionType(branchId, type, pageable);
        if (hasBranch && hasMethod)
            return cashbookTransactionRepository.findByBranchIdAndPaymentMethod(branchId, method, pageable);
        if (hasBranch)
            return cashbookTransactionRepository.findByBranchId(branchId, pageable);

        if (hasType && hasMethod)
            return cashbookTransactionRepository.findByTransactionTypeAndPaymentMethod(type, method, pageable);
        if (hasType)
            return cashbookTransactionRepository.findByTransactionType(type, pageable);
        if (hasMethod)
            return cashbookTransactionRepository.findByPaymentMethod(method, pageable);

        return cashbookTransactionRepository.findAll(pageable);
    }

    @Override
    public CashbookTransaction getTransactionById(Long id) {
        return cashbookTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu thu/chi với ID: " + id));
    }

    @Override
    @Transactional
    public CashbookTransaction createTransaction(CashbookDTO dto, Long createdBy) {
        Branch branch = branchRepository.findById(dto.getBranchId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi nhánh"));

        if (branch.getStatus() == com.g4fpt.sms.branch.entity.BranchStatus.INACTIVE) {
            throw new IllegalArgumentException("Chi nhánh đang ngừng hoạt động, không thể tạo phiếu thu/chi.");
        }

        Employee creator = employeeRepository.findById(createdBy)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người tạo"));

        CashbookTransaction transaction = CashbookTransaction.builder()
                .branch(branch)
                .transactionType(dto.getTransactionType())
                .paymentMethod(dto.getPaymentMethod())
                .amount(dto.getAmount())
                .referenceCode(dto.getReferenceCode())
                .description(dto.getDescription())
                .creator(creator)
                .status("PENDING") // Default to PENDING
                .build();

        return cashbookTransactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void approveTransaction(Long id) {
        CashbookTransaction tx = cashbookTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu"));
        tx.setStatus("COMPLETED");
        cashbookTransactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void rejectTransaction(Long id) {
        CashbookTransaction tx = cashbookTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu"));
        tx.setStatus("REJECTED");
        cashbookTransactionRepository.save(tx);
    }

    @Override
    public List<CashbookTransaction> getPendingTransactions(Long branchId) {
        if (branchId == null) {
            return cashbookTransactionRepository.findByStatusOrderByCreatedAtDesc("PENDING");
        }
        return cashbookTransactionRepository.findByBranchIdAndStatusOrderByCreatedAtDesc(branchId, "PENDING");
    }

    @Override
    public BigDecimal getBalance(Long branchId, String method) {
        BigDecimal totalIn = sumAmount(branchId, "IN", method);
        BigDecimal totalOut = sumAmount(branchId, "OUT", method);
        return totalIn.subtract(totalOut);
    }

    private BigDecimal sumAmount(Long branchId, String type, String method) {
        List<CashbookTransaction> list;
        if (branchId != null) {
            list = cashbookTransactionRepository.findByBranchIdAndTransactionTypeAndPaymentMethod(branchId, type,
                    method);
        } else {
            list = cashbookTransactionRepository.findByTransactionTypeAndPaymentMethod(type, method);
        }
        return list.stream()
                .filter(t -> "COMPLETED".equals(t.getStatus()))
                .map(CashbookTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public byte[] exportExcel(Long branchId, String type, String method, LocalDateTime startDate,
            LocalDateTime endDate) {
        Page<CashbookTransaction> transactionsPage = getTransactions(branchId, type, method, startDate, endDate, 1,
                Integer.MAX_VALUE);
        List<CashbookTransaction> transactions = transactionsPage.getContent();

        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Cashbook");

            // Header
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] columns = { "Mã phiếu", "Ngày tạo", "Chi nhánh", "Loại phiếu", "Phương thức", "Giá trị",
                    "Người tạo" };
            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data
            int rowIdx = 1;
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("dd/MM/yyyy HH:mm");
            for (CashbookTransaction tx : transactions) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0)
                        .setCellValue(tx.getReferenceCode() != null ? tx.getReferenceCode() : "CB-" + tx.getId());
                row.createCell(1).setCellValue(tx.getCreatedAt() != null ? tx.getCreatedAt().format(formatter) : "");
                row.createCell(2).setCellValue(tx.getBranch() != null ? tx.getBranch().getName() : "");
                row.createCell(3).setCellValue("IN".equals(tx.getTransactionType()) ? "Thu" : "Chi");
                row.createCell(4).setCellValue("CASH".equals(tx.getPaymentMethod()) ? "Tiền mặt" : "Chuyển khoản");
                row.createCell(5).setCellValue(tx.getAmount() != null ? tx.getAmount().doubleValue() : 0);
                row.createCell(6).setCellValue(tx.getCreator() != null ? tx.getCreator().getFullName() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Lỗi khi xuất file Excel", e);
        }
    }
}
