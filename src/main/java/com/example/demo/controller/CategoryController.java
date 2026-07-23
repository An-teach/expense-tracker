package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CategoryDto;
import com.example.demo.service.CategoryService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/settings/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 取得目前使用者的所有自訂分類標籤
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getUserCategories(Authentication authentication) {
        String username = authentication.getName(); // 從 JWT 中自動獲取登入者帳號
        return ResponseEntity.ok(categoryService.getCategoriesByUsername(username));
    }

    // 新增自訂標籤
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            Authentication authentication,
            @Valid @RequestBody CategoryDto dto) {
        String username = authentication.getName();
        return ResponseEntity.ok(categoryService.createCategory(username, dto));
    }
    
    // 修改自訂標籤
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            Authentication authentication,
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryDto dto) {
        String username = authentication.getName();
        return ResponseEntity.ok(categoryService.updateCategory(username, id, dto));
    }

    // 刪除自訂標籤
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(Authentication authentication, @PathVariable("id") Long id) {
        String username = authentication.getName();
        categoryService.deleteCategory(username, id);
        return ResponseEntity.ok(Map.of("message", "分類標籤刪除成功"));
    }
}