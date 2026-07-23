package com.example.demo.service;

import com.example.demo.dto.CategoryDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // 取得指定使用者的所有分類標籤
    public List<CategoryDto> getCategoriesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        List<Category> categories = categoryRepository.findByUserId(user.getId());

        // 如果是新使用者且完全沒有分類，自動建立預設分類
        if (categories.isEmpty()) {
            categories = createDefaultCategories(user);
        }

        return categories.stream().map(c -> CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .type(c.getType())
                .color(c.getColor())
                .build()).collect(Collectors.toList());
    }

    // 新增自訂分類
    public CategoryDto createCategory(String username, CategoryDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Category category = Category.builder()
                .user(user)
                .name(dto.getName())
                .type(dto.getType())
                .color(dto.getColor() != null ? dto.getColor() : "#64748b")
                .build();

        Category saved = categoryRepository.save(category);

        dto.setId(saved.getId());
        return dto;
    }

    // 自動建立預設分類標籤
    private List<Category> createDefaultCategories(User user) {
        List<Category> defaults = List.of(
            Category.builder().user(user).name("餐飲").type("EXPENSE").color("#ef4444").build(),
            Category.builder().user(user).name("交通").type("EXPENSE").color("#3b82f6").build(),
            Category.builder().user(user).name("娛樂").type("EXPENSE").color("#10b981").build(),
            Category.builder().user(user).name("購物").type("EXPENSE").color("#f59e0b").build(),
            Category.builder().user(user).name("薪水").type("INCOME").color("#8b5cf6").build(),
            Category.builder().user(user).name("副業").type("INCOME").color("#ec4899").build()
        );
        return categoryRepository.saveAll(defaults);
    }
    
    // 修改分類標籤
    public CategoryDto updateCategory(String username, Long categoryId, CategoryDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分類標籤不存在"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("無權限修改此標籤");
        }

        category.setName(dto.getName());
        category.setType(dto.getType());
        if (dto.getColor() != null) category.setColor(dto.getColor());

        Category updated = categoryRepository.save(category);
        return CategoryDto.builder()
                .id(updated.getId())
                .name(updated.getName())
                .type(updated.getType())
                .color(updated.getColor())
                .build();
    }

    // 刪除分類標籤
    public void deleteCategory(String username, Long categoryId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分類標籤不存在"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("無權限刪除此標籤");
        }

        categoryRepository.delete(category);
    }
}