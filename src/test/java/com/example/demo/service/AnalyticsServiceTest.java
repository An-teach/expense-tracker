package com.example.demo.service;

import com.example.demo.dto.CategorySummaryDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.ExpenseRecord;
import com.example.demo.entity.User;
import com.example.demo.repository.ExpenseRecordRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {

    @Mock
    private ExpenseRecordRepository recordRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).username("testuser").build();
    }

    @Test
    @DisplayName("測試：計算月度圓餅圖類別比例與金額正確")
    void getMonthlyPieChartData_Success() {
        Category food = Category.builder().name("餐飲").type("EXPENSE").color("#ef4444").build();
        Category transport = Category.builder().name("交通").type("EXPENSE").color("#3b82f6").build();

        ExpenseRecord r1 = ExpenseRecord.builder().category(food).amount(new BigDecimal("300.00")).build();
        ExpenseRecord r2 = ExpenseRecord.builder().category(food).amount(new BigDecimal("100.00")).build();
        ExpenseRecord r3 = ExpenseRecord.builder().category(transport).amount(new BigDecimal("100.00")).build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(recordRepository.findByUserIdAndRecordDateBetweenOrderByRecordDateDescCreatedAtDesc(
                eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(r1, r2, r3)); // 總支出 500 (餐飲400, 交通100)

        List<CategorySummaryDto> result = analyticsService.getMonthlyPieChartData("testuser", 2026, 7);

        assertNotNull(result);
        assertEquals(2, result.size());
        
        // 排序後第一名應該是餐飲 (400元 / 80%)
        assertEquals("餐飲", result.get(0).getCategoryName());
        assertEquals(new BigDecimal("400.00"), result.get(0).getTotalAmount());
        assertEquals(80.0, result.get(0).getPercentage());
    }
}