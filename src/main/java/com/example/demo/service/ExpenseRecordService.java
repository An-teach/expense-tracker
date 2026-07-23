package com.example.demo.service;

import com.example.demo.dto.ExpenseRecordDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.ExpenseRecord;
import com.example.demo.entity.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ExpenseRecordRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 💡 1. 引入 Transactional

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional // 💡 2. 加上這個註解！保持 Session 在 Service 執行期間持續開啟
public class ExpenseRecordService {

    @Autowired
    private ExpenseRecordRepository recordRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. 新增記帳紀錄
    public ExpenseRecordDto createRecord(String username, ExpenseRecordDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("指定的分類標籤不存在"));

        ExpenseRecord record = ExpenseRecord.builder()
                .user(user)
                .category(category)
                .recordDate(dto.getRecordDate())
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .build();

        ExpenseRecord saved = recordRepository.save(record);
        return mapToDto(saved);
    }

    // 2. 取得指定日期的紀錄
    @Transactional(readOnly = true)
    public List<ExpenseRecordDto> getRecordsByDate(String username, LocalDate date) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        return recordRepository.findByUserIdAndRecordDateOrderByCreatedAtDesc(user.getId(), date)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // 3. 取得指定月份的歷史紀錄
    @Transactional(readOnly = true)
    public List<ExpenseRecordDto> getMonthlyRecords(String username, int year, int month) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return recordRepository.findByUserIdAndRecordDateBetweenOrderByRecordDateDescCreatedAtDesc(
                        user.getId(), startDate, endDate)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // 4. 刪除紀錄
    public void deleteRecord(String username, Long recordId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        ExpenseRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("該筆紀錄不存在"));

        if (!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("無權限刪除此筆紀錄");
        }

        recordRepository.delete(record);
    }
    // 更新記帳紀錄
    public ExpenseRecordDto updateRecord(String username, Long recordId, ExpenseRecordDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        ExpenseRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("該筆紀錄不存在"));

        if (!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("無權限修改此筆紀錄");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("指定的分類標籤不存在"));

        record.setCategory(category);
        record.setRecordDate(dto.getRecordDate());
        record.setAmount(dto.getAmount());
        record.setDescription(dto.getDescription());

        ExpenseRecord updated = recordRepository.save(record);
        return mapToDto(updated);
    }
    private ExpenseRecordDto mapToDto(ExpenseRecord record) {
        return ExpenseRecordDto.builder()
                .id(record.getId())
                .categoryId(record.getCategory().getId())
                .categoryName(record.getCategory().getName())
                .categoryColor(record.getCategory().getColor())
                .categoryType(record.getCategory().getType())
                .recordDate(record.getRecordDate())
                .amount(record.getAmount())
                .description(record.getDescription())
                .build();
    }
}