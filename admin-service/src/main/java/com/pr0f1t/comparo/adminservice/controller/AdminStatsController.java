package com.pr0f1t.comparo.adminservice.controller;

import com.pr0f1t.comparo.adminservice.dto.StatsResponse;
import com.pr0f1t.comparo.adminservice.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final StatsService statsService;

    @GetMapping
    public ResponseEntity<StatsResponse> getPlatformStats(){
        StatsResponse stats = statsService.getLatestStats();
        return ResponseEntity.ok(stats);
    }

}
