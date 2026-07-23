package com.example.demo.service;

import com.example.demo.dto.CategoryDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();
    }

    @Test
    @DisplayName("測試：新使用者查詢分類時，應自動初始化預設分類列表")
    void getCategoriesByUsername_NewUser_ShouldCreateDefaults() {
        // Arrange (模擬情境)
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByUserId(1L)).thenReturn(Collections.emptyList()); // 模擬資料庫完全沒有標籤
        
        // 模擬 saveAll 會回傳預設的 6 個分類
        when(categoryRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (執行測試目標)
        List<CategoryDto> result = categoryService.getCategoriesByUsername("testuser");

        // Assert (斷言驗證結果)
        assertNotNull(result);
        assertEquals(6, result.size()); // 驗證是否自動產出了 6 個預設標籤
        verify(categoryRepository, times(1)).saveAll(any()); // 驗證 saveAll 確實被呼叫一次
    }

    @Test
    @DisplayName("測試：新增自訂分類成功")
    void createCategory_Success() {
        // Arrange
        CategoryDto inputDto = CategoryDto.builder()
                .name("健身")
                .type("EXPENSE")
                .color("#000000")
                .build();

        Category savedCategory = Category.builder()
                .id(10L)
                .user(testUser)
                .name("健身")
                .type("EXPENSE")
                .color("#000000")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // Act
        CategoryDto result = categoryService.createCategory("testuser", inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("健身", result.getName());
    }
}