package com.internship.tool.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping("/create")
    public ResponseEntity<DfdRecord> create(@RequestBody DfdRecord record) {
        DfdRecord saved = service.create(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ─── GET /all → 200 OK ────────────────────────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<List<DfdRecord>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ─── GET /{id} → 200 OK or 404 NOT FOUND ──────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
                .filter(r -> !Boolean.TRUE.equals(r.getDeleted()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // ─── PUT /{id} → 200 OK or 404 NOT FOUND ──────────────────────────────────
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
    @GetMapping("/search")
    public ResponseEntity<List<DfdRecord>> search(@RequestParam String q) {
        List<DfdRecord> results = repository.findByTitleContainingIgnoreCaseAndDeletedFalse(q);
        return ResponseEntity.ok(results);
    }

    // ─── GET /stats → 200 OK ──────────────────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(service.getStats());
    }
}
