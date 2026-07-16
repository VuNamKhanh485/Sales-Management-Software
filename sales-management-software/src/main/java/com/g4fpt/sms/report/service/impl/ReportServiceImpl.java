package com.g4fpt.sms.report.service.impl;

import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.payment.entity.CashbookTransaction;
import com.g4fpt.sms.payment.repository.CashbookTransactionRepository;
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
}
