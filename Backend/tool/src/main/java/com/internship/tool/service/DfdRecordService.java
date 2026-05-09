package com.internship.tool.service;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.internship.tool.entity.DfdRecord;
import com.internship.tool.repository.DfdRecordRepository;

@Service
public class DfdRecordService {

    @Autowired
    private DfdRecordRepository repository;


    @Autowired
    private AiServiceClient aiService;

    @org.springframework.cache.annotation.CacheEvict(value = "dfd_records", allEntries = true)
    public DfdRecord create(DfdRecord record) {
        String aiAnalysis = aiService.getAiResponse(record.getDescription());
        if (aiAnalysis != null) {
            record.setDescription(record.getDescription() + "\n\n[AI Analysis]: " + aiAnalysis);
        }
        
        return repository.save(record);
    }

    @org.springframework.cache.annotation.Cacheable(value = "dfd_records")
    public List<DfdRecord> getAll() {
        return repository.findByDeletedFalse();
    }

    @org.springframework.cache.annotation.CacheEvict(value = "dfd_records", allEntries = true)
    public DfdRecord update(Long id, DfdRecord updatedRecord) {
        DfdRecord existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        existing.setTitle(updatedRecord.getTitle());
        existing.setDescription(updatedRecord.getDescription());
        existing.setStatus(updatedRecord.getStatus());
        if (updatedRecord.getPriority() != null) {
            existing.setPriority(updatedRecord.getPriority());
        }

        return repository.save(existing);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "dfd_records", allEntries = true)
    public void softDelete(Long id) {
        DfdRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        record.setDeleted(true);
        repository.save(record);
    }

    public Map<String, Object> getStats() {
        long total = repository.count();
        long active = repository.findByDeletedFalse().stream()
                .filter(r -> "ACTIVE".equals(r.getStatus()))
                .count();
        long deleted = repository.findAll().stream()
                .filter(r -> Boolean.TRUE.equals(r.getDeleted()))
                .count();
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        long recent = repository.findAll().stream()
                .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().isAfter(oneWeekAgo))
                .count();

        return Map.of(
                "total", total,
                "active", active,
                "deleted", deleted,
                "recent", recent);
    }
}
