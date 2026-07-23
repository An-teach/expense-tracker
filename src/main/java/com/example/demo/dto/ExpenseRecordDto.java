package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRecordDto {

    private Long id;

    @NotNull(message = "類別 ID 不能為空")
    private Long categoryId;

    private String categoryName;  // 供前端顯示用
    private String categoryColor; // 供前端顯示用
    private String categoryType;  // EXPENSE 或 INCOME

    @NotNull(message = "記帳日期不能為空")
    private LocalDate recordDate;

    @NotNull(message = "金額不能為空")
    @DecimalMin(value = "0.01", message = "金額必須大於 0")
    private BigDecimal amount;

    private String description;
}