package com.trinetra.verdict.controller;

import com.trinetra.verdict.service.LogisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/units")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UnitHistoryController {
    private final LogisticsService logisticsService;

    @GetMapping("/{unitId}/history")
    public ResponseEntity<?> history(@PathVariable String unitId) {
        try {
            return ResponseEntity.ok(logisticsService.unitHistory(unitId));
        } catch (IllegalArgumentException error) {
            Map<String, String> body = new LinkedHashMap<>();
            body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
            body.put("message", error.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
    }
}