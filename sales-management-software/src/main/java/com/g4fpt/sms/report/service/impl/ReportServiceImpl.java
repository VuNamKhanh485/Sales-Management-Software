package com.g4fpt.sms.report.service.impl;

import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.payment.entity.CashbookTransaction;
import com.g4fpt.sms.payment.repository.CashbookTransactionRepository;
import com.g4fpt.sms.report.dto.EmployeeSalesDTO;
import com.g4fpt.sms.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderTransactionRepository orderTransactionRepository;
    private final CashbookTransactionRepository cashbookTransactionRepository;

    @Override
    public Map<String, Object> getProfitReport(Long branchId, LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderTransaction> transactions = orderTransactionRepository.findCompletedTransactionsForReport(branchId, startDate, endDate);
        
        List<CashbookTransaction> cashbookTransactions;
        if (branchId != null) {
            cashbookTransactions = cashbookTransactionRepository.findByBranchIdAndCreatedAtBetweenOrderByCreatedAtAsc(branchId, startDate, endDate);
        } else {
            cashbookTransactions = cashbookTransactionRepository.findByCreatedAtBetweenOrderByCreatedAtAsc(startDate, endDate);
        }

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        // Group by Date for charting
        Map<String, BigDecimal> revenueByDate = new LinkedHashMap<>();
        Map<String, BigDecimal> expenseByDate = new LinkedHashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Pre-fill all dates in range to ensure chronological order and no missing days
        LocalDate start = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String dateKey = date.format(formatter);
            revenueByDate.put(dateKey, BigDecimal.ZERO);
            expenseByDate.put(dateKey, BigDecimal.ZERO);
        }

        for (OrderTransaction tx : transactions) {
            String dateKey = tx.getCreatedAt().format(formatter);
            // In case of any timezone issues or out-of-range keys
            revenueByDate.putIfAbsent(dateKey, BigDecimal.ZERO);
            expenseByDate.putIfAbsent(dateKey, BigDecimal.ZERO);

            if ("SALE".equals(tx.getTransactionType())) {
                totalRevenue = totalRevenue.add(tx.getFinalAmount());
                revenueByDate.put(dateKey, revenueByDate.get(dateKey).add(tx.getFinalAmount()));
            } else if ("IMPORT".equals(tx.getTransactionType()) || "RETURN".equals(tx.getTransactionType())) {
                totalExpense = totalExpense.add(tx.getFinalAmount());
                expenseByDate.put(dateKey, expenseByDate.get(dateKey).add(tx.getFinalAmount()));
            } else if ("TRANSFER".equals(tx.getTransactionType())) {
                if (branchId == null) {
                    totalRevenue = totalRevenue.add(tx.getFinalAmount());
                    revenueByDate.put(dateKey, revenueByDate.get(dateKey).add(tx.getFinalAmount()));
                    totalExpense = totalExpense.add(tx.getFinalAmount());
                    expenseByDate.put(dateKey, expenseByDate.get(dateKey).add(tx.getFinalAmount()));
                } else {
                    if (branchId.equals(tx.getFromBranchId())) {
                        totalRevenue = totalRevenue.add(tx.getFinalAmount());
                        revenueByDate.put(dateKey, revenueByDate.get(dateKey).add(tx.getFinalAmount()));
                    }
                    if (branchId.equals(tx.getToBranchId())) {
                        totalExpense = totalExpense.add(tx.getFinalAmount());
                        expenseByDate.put(dateKey, expenseByDate.get(dateKey).add(tx.getFinalAmount()));
                    }
                }
            }
        }

        for (CashbookTransaction ctx : cashbookTransactions) {
            String dateKey = ctx.getCreatedAt().format(formatter);
            revenueByDate.putIfAbsent(dateKey, BigDecimal.ZERO);
            expenseByDate.putIfAbsent(dateKey, BigDecimal.ZERO);

            if ("IN".equals(ctx.getTransactionType())) {
                totalRevenue = totalRevenue.add(ctx.getAmount());
                revenueByDate.put(dateKey, revenueByDate.get(dateKey).add(ctx.getAmount()));
            } else if ("OUT".equals(ctx.getTransactionType())) {
                totalExpense = totalExpense.add(ctx.getAmount());
                expenseByDate.put(dateKey, expenseByDate.get(dateKey).add(ctx.getAmount()));
            }
        }

        BigDecimal netProfit = totalRevenue.subtract(totalExpense);

        // Prepare chart data lists
        List<String> labels = new ArrayList<>(revenueByDate.keySet());
        List<BigDecimal> revenueData = new ArrayList<>();
        List<BigDecimal> expenseData = new ArrayList<>();
        List<BigDecimal> profitData = new ArrayList<>();

        for (String label : labels) {
            BigDecimal rev = revenueByDate.get(label);
            BigDecimal exp = expenseByDate.get(label);
            revenueData.add(rev);
            expenseData.add(exp);
            profitData.add(rev.subtract(exp));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("totalExpense", totalExpense);
        result.put("netProfit", netProfit);
        result.put("labels", labels);
        result.put("revenueData", revenueData);
        result.put("expenseData", expenseData);
        result.put("profitData", profitData);

        return result;
    }

    @Override
    public List<com.g4fpt.sms.report.dto.CashflowDetailDTO> getDetailedCashflow(Long branchId, LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderTransaction> transactions = orderTransactionRepository.findCompletedTransactionsForReport(branchId, startDate, endDate);
        
        List<CashbookTransaction> cashbookTransactions;
        if (branchId != null) {
            cashbookTransactions = cashbookTransactionRepository.findByBranchIdAndCreatedAtBetweenOrderByCreatedAtAsc(branchId, startDate, endDate);
        } else {
            cashbookTransactions = cashbookTransactionRepository.findByCreatedAtBetweenOrderByCreatedAtAsc(startDate, endDate);
        }

        List<com.g4fpt.sms.report.dto.CashflowDetailDTO> details = new ArrayList<>();

        for (OrderTransaction tx : transactions) {
            com.g4fpt.sms.report.dto.CashflowDetailDTO dto = new com.g4fpt.sms.report.dto.CashflowDetailDTO();
            dto.setCreatedAt(tx.getCreatedAt());
            dto.setCode(tx.getCode());
            
            if ("SALE".equals(tx.getTransactionType())) {
                dto.setType("Bán hàng");
                dto.setAmountIn(tx.getFinalAmount());
                dto.setAmountOut(BigDecimal.ZERO);
            } else if ("IMPORT".equals(tx.getTransactionType())) {
                dto.setType("Nhập hàng");
                dto.setAmountIn(BigDecimal.ZERO);
                dto.setAmountOut(tx.getFinalAmount());
            } else if ("RETURN".equals(tx.getTransactionType())) {
                dto.setType("Trả hàng");
                dto.setAmountIn(BigDecimal.ZERO);
                dto.setAmountOut(tx.getFinalAmount());
            } else if ("TRANSFER".equals(tx.getTransactionType())) {
                if (branchId == null) {
                    // Record both sides for global view
                    com.g4fpt.sms.report.dto.CashflowDetailDTO dtoOut = new com.g4fpt.sms.report.dto.CashflowDetailDTO();
                    dtoOut.setCreatedAt(tx.getCreatedAt());
                    dtoOut.setCode(tx.getCode());
                    dtoOut.setType("Chuyển kho (Xuất)");
                    dtoOut.setAmountIn(tx.getFinalAmount());
                    dtoOut.setAmountOut(BigDecimal.ZERO);
                    dtoOut.setDescription("Thu tiền chuyển kho cho chi nhánh khác");
                    details.add(dtoOut);
                    
                    com.g4fpt.sms.report.dto.CashflowDetailDTO dtoIn = new com.g4fpt.sms.report.dto.CashflowDetailDTO();
                    dtoIn.setCreatedAt(tx.getCreatedAt());
                    dtoIn.setCode(tx.getCode());
                    dtoIn.setType("Chuyển kho (Nhập)");
                    dtoIn.setAmountIn(BigDecimal.ZERO);
                    dtoIn.setAmountOut(tx.getFinalAmount());
                    dtoIn.setDescription("Trả tiền nhận hàng chuyển kho");
                    details.add(dtoIn);
                    continue;
                } else {
                    if (branchId.equals(tx.getFromBranchId())) {
                        dto.setType("Chuyển kho (Xuất)");
                        dto.setAmountIn(tx.getFinalAmount());
                        dto.setAmountOut(BigDecimal.ZERO);
                        dto.setDescription("Thu tiền chuyển kho");
                    } else if (branchId.equals(tx.getToBranchId())) {
                        dto.setType("Chuyển kho (Nhập)");
                        dto.setAmountIn(BigDecimal.ZERO);
                        dto.setAmountOut(tx.getFinalAmount());
                        dto.setDescription("Trả tiền nhận hàng chuyển kho");
                    } else {
                        continue;
                    }
                }
            } else {
                continue; // Ignore other types if any
            }
            dto.setDescription(tx.getNote());
            details.add(dto);
        }

        for (CashbookTransaction ctx : cashbookTransactions) {
            com.g4fpt.sms.report.dto.CashflowDetailDTO dto = new com.g4fpt.sms.report.dto.CashflowDetailDTO();
            dto.setCreatedAt(ctx.getCreatedAt());
            dto.setCode(ctx.getReferenceCode() != null ? ctx.getReferenceCode() : "CB-" + ctx.getId());
            
            if ("IN".equals(ctx.getTransactionType())) {
                dto.setType("Sổ quỹ - Thu");
                dto.setAmountIn(ctx.getAmount());
                dto.setAmountOut(BigDecimal.ZERO);
            } else if ("OUT".equals(ctx.getTransactionType())) {
                dto.setType("Sổ quỹ - Chi");
                dto.setAmountIn(BigDecimal.ZERO);
                dto.setAmountOut(ctx.getAmount());
            }
            dto.setDescription(ctx.getDescription());
            details.add(dto);
        }

        details.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())); // Mới nhất lên đầu
        return details;
    }

    @Override
    public List<EmployeeSalesDTO> getEmployeeSalesReport(Long branchId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderTransactionRepository.getEmployeeSalesReport(branchId, startDate, endDate);
    }

    @Override
    public List<OrderTransaction> getEmployeeSalesDetails(Long employeeId, Long branchId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderTransactionRepository.findEmployeeSalesDetails(employeeId, branchId, startDate, endDate);
    }



    @Override
    public org.springframework.data.domain.Page<com.g4fpt.sms.report.dto.EmployeeOrderSalesDTO> getDetailedOrderSalesPage(Long branchId, Long employeeId, LocalDateTime startDate, LocalDateTime endDate, org.springframework.data.domain.Pageable pageable) {
        return orderTransactionRepository.getDetailedOrderSalesPage(branchId, employeeId, startDate, endDate, pageable);
    }

    @Override
    public List<com.g4fpt.sms.report.dto.EmployeeOrderSalesDTO> getDetailedOrderSalesList(Long branchId, Long employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderTransactionRepository.getDetailedOrderSalesList(branchId, employeeId, startDate, endDate);
    }

    @Override
    public List<Object[]> getDetailedOrderSalesTotals(Long branchId, Long employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderTransactionRepository.getDetailedOrderSalesTotals(branchId, employeeId, startDate, endDate);
    }



    @Override
    public byte[] exportDetailedOrderSalesToExcel(List<com.g4fpt.sms.report.dto.EmployeeOrderSalesDTO> data) throws java.io.IOException {
        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
             java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Bang ke ban hang");
            
            // Header Row
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] columns = {"TT", "Thời gian", "Chi nhánh", "Nhân viên", "Mã đơn hàng", "Thành tiền"};
            
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Format for date
            org.apache.poi.ss.usermodel.CellStyle dateStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));

            // Data Rows
            int rowIdx = 1;
            double totalRevenue = 0.0;
            for (com.g4fpt.sms.report.dto.EmployeeOrderSalesDTO order : data) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx);
                
                row.createCell(0).setCellValue(rowIdx);
                
                org.apache.poi.ss.usermodel.Cell dateCell = row.createCell(1);
                if (order.getCreatedAt() != null) {
                    dateCell.setCellValue(java.sql.Timestamp.valueOf(order.getCreatedAt()));
                    dateCell.setCellStyle(dateStyle);
                }
                
                row.createCell(2).setCellValue(order.getBranchName() != null ? order.getBranchName() : "");
                row.createCell(3).setCellValue(order.getEmployeeName() != null ? order.getEmployeeName() : "");
                row.createCell(4).setCellValue(order.getOrderCode() != null ? order.getOrderCode() : "");
                
                double amount = order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0.0;
                row.createCell(5).setCellValue(amount);
                totalRevenue += amount;
                
                rowIdx++;
            }
            
            // Total Row
            org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowIdx);
            org.apache.poi.ss.usermodel.Cell totalLabelCell = totalRow.createCell(4);
            totalLabelCell.setCellValue("Tổng doanh thu:");
            totalLabelCell.setCellStyle(headerStyle);
            
            org.apache.poi.ss.usermodel.Cell totalValueCell = totalRow.createCell(5);
            totalValueCell.setCellValue(totalRevenue);
            totalValueCell.setCellStyle(headerStyle);

            // Auto size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
}
