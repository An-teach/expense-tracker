package com.example.demo.controller;

import com.example.demo.dto.CategoryDto;
import com.example.demo.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("測試：未登入者存取分類 API 應被拒絕 (403 Unauthorized)")
    void getCategories_Unauthenticated_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/settings/categories"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser") // 模擬登入帳號為 testuser 的使用者
    @DisplayName("測試：已登入者可成功取得分類清單")
    void getCategories_Authenticated_ShouldReturnList() throws Exception {
        List<CategoryDto> mockList = List.of(
                CategoryDto.builder().id(1L).name("餐飲").type("EXPENSE").color("#ef4444").build()
        );

        when(categoryService.getCategoriesByUsername("testuser")).thenReturn(mockList);

        mockMvc.perform(get("/api/settings/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("餐飲"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("測試：新增分類時若格式不符應拋出錯誤 (400 Bad Request)")
    void createCategory_InvalidData_ShouldReturn400() throws Exception {
        CategoryDto invalidDto = new CategoryDto(); // 沒有填 name 與 type

        mockMvc.perform(post("/api/settings/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}