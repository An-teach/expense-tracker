package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.ExpenseRecord;

public interface ExpenseRecordRepository extends JpaRepository<ExpenseRecord, Long> {
    List<ExpenseRecord> findByUserIdAndRecordDate(Long userId, LocalDate date);
    
    // 查詢特定使用者在特定日期的紀錄
    List<ExpenseRecord> findByUserIdAndRecordDateOrderByCreatedAtDesc(Long userId, LocalDate date);

    // 查詢特定使用者在日期區間內(例如某個月1號到底號)的紀錄
    List<ExpenseRecord> findByUserIdAndRecordDateBetweenOrderByRecordDateDescCreatedAtDesc(
            Long userId, LocalDate startDate, LocalDate endDate);
}