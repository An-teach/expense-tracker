package com.example.demo.service;

import com.example.demo.dto.CategorySummaryDto;
import com.example.demo.entity.ExpenseRecord;
import com.example.demo.entity.User;
import com.example.demo.repository.ExpenseRecordRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 💡 1. 引入 Transactional

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) // 💡 2. 加上這個註解，解決 LazyInitializationException
public class AnalyticsService {

    @Autowired
    private ExpenseRecordRepository recordRepository;

    @Autowired
    private UserRepository userRepository;

    // 取得指定月份各「支出」類別的金額與佔比統計 (供圓餅圖繪製)
    public List<CategorySummaryDto> getMonthlyPieChartData(String username, int year, int month) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 1. 撈出該月份該使用者的所有紀錄
        List<ExpenseRecord> records = recordRepository
                .findByUserIdAndRecordDateBetweenOrderByRecordDateDescCreatedAtDesc(
                        user.getId(), startDate, endDate);

        // 2. 僅過濾出「EXPENSE (支出)」紀錄
        List<ExpenseRecord> expenseRecords = records.stream()
                .filter(r -> "EXPENSE".equalsIgnoreCase(r.getCategory().getType()))
                .collect(Collectors.toList());

        // 3. 計算月度總支出金額
        BigDecimal totalExpense = expenseRecords.stream()
                .map(ExpenseRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalExpense.compareTo(BigDecimal.ZERO) == 0) {
            return Collections.emptyList(); // 當月無支出紀錄
        }

        // 4. 按 Category 分組並加總金額
        Map<String, Map<String, Object>> categoryGroup = new HashMap<>();

        for (ExpenseRecord record : expenseRecords) {
            String catName = record.getCategory().getName();
            String catColor = record.getCategory().getColor();

            categoryGroup.putIfAbsent(catName, new HashMap<>(Map.of(
                    "color", catColor != null ? catColor : "#64748b",
                    "amount", BigDecimal.ZERO
            )));

            BigDecimal currentSum = (BigDecimal) categoryGroup.get(catName).get("amount");
            categoryGroup.get(catName).put("amount", currentSum.add(record.getAmount()));
        }

        // 5. 組裝成 DTO 並計算百分比
        List<CategorySummaryDto> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : categoryGroup.entrySet()) {
            String catName = entry.getKey();
            String catColor = (String) entry.getValue().get("color");
            BigDecimal catAmount = (BigDecimal) entry.getValue().get("amount");

            double percentage = catAmount
                    .multiply(new BigDecimal(100))
                    .divide(totalExpense, 2, RoundingMode.HALF_UP)
                    .doubleValue();

            result.add(CategorySummaryDto.builder()
                    .categoryName(catName)
                    .categoryColor(catColor)
                    .totalAmount(catAmount)
                    .percentage(percentage)
                    .build());
        }

        result.sort((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()));
        return result;
    }
}