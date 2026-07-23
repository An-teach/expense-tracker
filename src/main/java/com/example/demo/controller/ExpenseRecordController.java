package com.example.demo.controller;

import com.example.demo.dto.ExpenseRecordDto;
import com.example.demo.service.ExpenseRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/records")
public class ExpenseRecordController {

    @Autowired
    private ExpenseRecordService recordService;

    // 新增單筆紀錄
    @PostMapping
    public ResponseEntity<ExpenseRecordDto> createRecord(
            Authentication authentication,
            @Valid @RequestBody ExpenseRecordDto dto) {
        String username = authentication.getName();
        return ResponseEntity.ok(recordService.createRecord(username, dto));
    }

    // 按日期查詢紀錄 (/api/records?date=YYYY-MM-DD)
    @GetMapping
    public ResponseEntity<List<ExpenseRecordDto>> getRecordsByDate(
            Authentication authentication,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String username = authentication.getName();
        return ResponseEntity.ok(recordService.getRecordsByDate(username, date));
    }

    // 按月份查詢歷史明細 (/api/records/history?year=2026&month=7)
    @GetMapping("/history")
    public ResponseEntity<List<ExpenseRecordDto>> getMonthlyRecords(
            Authentication authentication,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        String username = authentication.getName();
        return ResponseEntity.ok(recordService.getMonthlyRecords(username, year, month));
    }

    // 刪除紀錄
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecord(Authentication authentication, @PathVariable("id") Long id) {
        String username = authentication.getName();
        recordService.deleteRecord(username, id);
        return ResponseEntity.ok(Map.of("message", "紀錄刪除成功"));
    }
    
    // 修改單筆紀錄
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseRecordDto> updateRecord(
            Authentication authentication,
            @PathVariable("id") Long id,
            @Valid @RequestBody ExpenseRecordDto dto) {
        String username = authentication.getName();
        return ResponseEntity.ok(recordService.updateRecord(username, id, dto));
    }
}