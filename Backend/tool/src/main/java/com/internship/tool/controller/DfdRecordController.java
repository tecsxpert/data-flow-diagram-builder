package com.internship.tool.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.internship.tool.entity.DfdRecord;
import com.internship.tool.repository.DfdRecordRepository;
import com.internship.tool.service.DfdRecordService;

@RestController
@RequestMapping("/api/diagram")
public class DfdRecordController {

    @Autowired
    private DfdRecordService service;

    @Autowired
    private DfdRecordRepository repository;

    // ─── POST /create → 201 CREATED ───────────────────────────────────────────
    @Operation(summary = "Create a new DFD record")
    @PostMapping("/create")
    public ResponseEntity<DfdRecord> create(@RequestBody DfdRecord record) {
        DfdRecord saved = service.create(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ─── GET /all → 200 OK ────────────────────────────────────────────────────
    @Operation(summary = "Get all records")
    @GetMapping("/all")
    public ResponseEntity<Page<DfdRecord>> getAll(Pageable pageable) {
        return ResponseEntity.ok(repository.findByDeletedFalse(pageable));
    }

    // ─── GET /{id} → 200 OK or 404 NOT FOUND ──────────────────────────────────
    @Operation(summary = "Get a record by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
                .filter(r -> !Boolean.TRUE.equals(r.getDeleted()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // ─── PUT /{id} → 200 OK or 404 NOT FOUND ──────────────────────────────────
    @Operation(summary = "Update a record")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody DfdRecord updatedRecord
    ) {
        try {
            return ResponseEntity.ok(service.update(id, updatedRecord));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found");
        }
    }

    // ─── DELETE /{id} → 200 OK or 404 NOT FOUND ───────────────────────────────
    @Operation(summary = "Delete a record")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.softDelete(id);
            return ResponseEntity.ok("Deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found");
        }
    }

    // ─── GET /search?q= → 200 OK ──────────────────────────────────────────────
    @Operation(summary = "Search records by title")
    @GetMapping("/search")
    public ResponseEntity<List<DfdRecord>> search(@RequestParam String q) {
        List<DfdRecord> results = repository.findByTitleContainingIgnoreCaseAndDeletedFalse(q);
        return ResponseEntity.ok(results);
    }

    // ─── GET /stats → 200 OK ──────────────────────────────────────────────────
    @Operation(summary = "Get statistics")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(service.getStats());
    }

    // ─── GET /analytics → 200 OK ──────────────────────────────────────────────
    @Operation(summary = "Get analytics data")
    @GetMapping("/analytics")
    public List<Map<String, Object>> getAnalytics(@RequestParam int days) {
        List<DfdRecord> records = repository.findAll().stream()
                .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().isAfter(LocalDateTime.now().minusDays(days)))
                .toList();

        Map<String, Long> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().toLocalDate().toString(),
                        Collectors.counting()
                ));

        return grouped.entrySet().stream()
                .map(e -> Map.of(
                        "date", e.getKey(),
                        "count", e.getValue()
                ))
                .toList();
    }
}
