package com.example.demo.controller;

import com.example.demo.dto.CategorySummaryDto;
import com.example.demo.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // GET /api/analytics/monthly-pie?year=2026&month=7
    @GetMapping("/monthly-pie")
    public ResponseEntity<List<CategorySummaryDto>> getMonthlyPieChartData(
            Authentication authentication,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        String username = authentication.getName();
        return ResponseEntity.ok(analyticsService.getMonthlyPieChartData(username, year, month));
    }
}