package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private Long id;

    @NotBlank(message = "類別名稱不能為空")
    private String name;

    @NotBlank(message = "類別類型不能為空")
    @Pattern(regexp = "^(EXPENSE|INCOME)$", message = "類型必須為 EXPENSE 或 INCOME")
    private String type;

    private String color;
}