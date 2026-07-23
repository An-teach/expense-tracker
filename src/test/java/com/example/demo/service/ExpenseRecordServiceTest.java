package com.example.demo.service;

import com.example.demo.dto.ExpenseRecordDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.ExpenseRecord;
import com.example.demo.entity.User;
import com.example.demo.repository.CategoryRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExpenseRecordServiceTest {

    @Mock
    private ExpenseRecordRepository recordRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseRecordService recordService;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).username("testuser").build();
        testCategory = Category.builder().id(1L).name("餐飲").type("EXPENSE").user(testUser).build();
    }

    @Test
    @DisplayName("測試：新增記帳紀錄成功")
    void createRecord_Success() {
        ExpenseRecordDto inputDto = ExpenseRecordDto.builder()
                .categoryId(1L)
                .recordDate(LocalDate.now())
                .amount(new BigDecimal("150.00"))
                .description("午餐便當")
                .build();

        ExpenseRecord savedRecord = ExpenseRecord.builder()
                .id(100L)
                .user(testUser)
                .category(testCategory)
                .recordDate(LocalDate.now())
                .amount(new BigDecimal("150.00"))
                .description("午餐便當")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(recordRepository.save(any(ExpenseRecord.class))).thenReturn(savedRecord);

        ExpenseRecordDto result = recordService.createRecord("testuser", inputDto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(new BigDecimal("150.00"), result.getAmount());
        assertEquals("餐飲", result.getCategoryName());
    }
}