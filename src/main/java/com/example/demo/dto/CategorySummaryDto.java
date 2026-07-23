package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySummaryDto {

    private String categoryName;
    private String categoryColor;
    private BigDecimal totalAmount; // 該類別總金額
    private Double percentage;      // 占總支出的百分比 (0~100)
}