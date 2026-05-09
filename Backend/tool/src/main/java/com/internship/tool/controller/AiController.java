package com.internship.tool.controller;

import com.internship.tool.dto.AiRequest;
import com.internship.tool.service.AiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/diagram/ai")
@CrossOrigin(origins = "*")
public class AiController {

    @Autowired
    private AiService aiService;

    /** GET /ai/ — Service info */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(Map.of(
            "message", "AI Service Running (Java)",
            "endpoints", new String[]{
                "POST /ai/describe",
                "POST /ai/recommend",
                "POST /ai/generate-report",
                "GET  /ai/health"
            }
        ));
    }

    /** GET /ai/health — Health check with Redis status */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(aiService.health());
    }

    /** POST /ai/describe — Describe a DFD component */
    @PostMapping("/describe")
    public ResponseEntity<Map<String, Object>> describe(@Valid @RequestBody AiRequest request) {
        return ResponseEntity.ok(aiService.describe(request.getInput()));
    }

    /** POST /ai/recommend — Get AI recommendations */
    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> recommend(@Valid @RequestBody AiRequest request) {
        return ResponseEntity.ok(aiService.recommend(request.getInput()));
    }

    /** POST /ai/generate-report — Generate a full system report */
    @PostMapping("/generate-report")
    public ResponseEntity<Map<String, Object>> generateReport(@Valid @RequestBody AiRequest request) {
        return ResponseEntity.ok(aiService.generateReport(request.getInput()));
    }
}
