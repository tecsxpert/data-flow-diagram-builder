package com.internship.tool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);
    private static final int CACHE_TTL_SECONDS = 3600;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // ─── Shared Helpers ──────────────────────────────────────────────────────────

    /** Generate a SHA-256 cache key from route + input text */
    private String generateKey(String text, String route) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((route + text).getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            return route + "_" + text.hashCode();
        }
    }

    /** Try to get a value from Redis; returns null if Redis is unavailable. */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getCached(String key) {
        try {
            String raw = redisTemplate.opsForValue().get(key);
            if (raw != null) {
                return objectMapper.readValue(raw, Map.class);
            }
        } catch (Exception e) {
            log.warn("Redis cache read failed: {}", e.getMessage());
        }
        return null;
    }

    /** Try to store a value in Redis; silently skip if unavailable. */
    private void setCache(String key, Map<String, Object> value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Redis cache write failed: {}", e.getMessage());
        }
    }

    // ─── Describe ────────────────────────────────────────────────────────────────

    public Map<String, Object> describe(String input) {
        long start = System.currentTimeMillis();
        String key = generateKey(input, "describe");

        Map<String, Object> cached = getCached(key);
        if (cached != null) {
            cached.put("cached", true);
            cached.put("response_time", (System.currentTimeMillis() - start) / 1000.0);
            return cached;
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("input", input);
        response.put("description", buildDescription(input));
        response.put("key_components", extractComponents(input));
        response.put("complexity", assessComplexity(input));
        response.put("generated_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("cached", false);

        setCache(key, response);
        response.put("response_time", (System.currentTimeMillis() - start) / 1000.0);
        return response;
    }

    // ─── Recommend ───────────────────────────────────────────────────────────────

    public Map<String, Object> recommend(String input) {
        long start = System.currentTimeMillis();
        String key = generateKey(input, "recommend");

        Map<String, Object> cached = getCached(key);
        if (cached != null) {
            cached.put("cached", true);
            cached.put("response_time", (System.currentTimeMillis() - start) / 1000.0);
            return cached;
        }

        List<Map<String, String>> recommendations = new ArrayList<>();

        recommendations.add(Map.of(
            "action_type", "Optimization",
            "description", "Implement caching layers for frequently accessed " + input + " data flows.",
            "priority", "High"
        ));
        recommendations.add(Map.of(
            "action_type", "Security",
            "description", "Add input validation and encryption to all " + input + " endpoints.",
            "priority", "High"
        ));
        recommendations.add(Map.of(
            "action_type", "Scalability",
            "description", "Introduce message queues to handle peak loads in " + input + " processing.",
            "priority", "Medium"
        ));
        recommendations.add(Map.of(
            "action_type", "Monitoring",
            "description", "Set up real-time dashboards and alerts for " + input + " service health.",
            "priority", "Low"
        ));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("input", input);
        response.put("recommendations", recommendations);
        response.put("total", recommendations.size());
        response.put("generated_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("cached", false);

        setCache(key, response);
        response.put("response_time", (System.currentTimeMillis() - start) / 1000.0);
        return response;
    }

    // ─── Generate Report ─────────────────────────────────────────────────────────

    public Map<String, Object> generateReport(String input) {
        long start = System.currentTimeMillis();
        String key = generateKey(input, "report");

        Map<String, Object> cached = getCached(key);
        if (cached != null) {
            cached.put("cached", true);
            cached.put("response_time", (System.currentTimeMillis() - start) / 1000.0);
            return cached;
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("title", input + " — System Report");
        response.put("summary", buildSummary(input));
        response.put("overview", "The " + input + " system manages core operations, data integrity, and user interactions effectively.");
        response.put("key_items", List.of(
            "User Management & Authentication",
            "Data Flow Processing",
            "System Security Controls",
            "Performance & Caching",
            "Reporting & Analytics"
        ));
        response.put("recommendations", List.of(
            "Enhance horizontal scalability with load balancing",
            "Add end-to-end encryption for sensitive data flows",
            "Integrate an analytics dashboard for usage insights",
            "Implement automated backups and disaster recovery"
        ));
        response.put("generated_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("cached", false);

        setCache(key, response);
        response.put("response_time", (System.currentTimeMillis() - start) / 1000.0);
        return response;
    }

    // ─── Health Check ────────────────────────────────────────────────────────────

    public Map<String, Object> health() {
        String redisStatus;
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            redisStatus = "connected";
        } catch (Exception e) {
            redisStatus = "disconnected";
        }

        return Map.of(
            "status", "healthy",
            "service", "AI Service (Java)",
            "redis", redisStatus,
            "timestamp", Instant.now().toString()
        );
    }

    // ─── Private Analysis Helpers ─────────────────────────────────────────────────

    private String buildDescription(String input) {
        return String.format(
            "%s is a structured data flow component responsible for managing information " +
            "between system boundaries. It coordinates input validation, business logic execution, " +
            "and output transformation to ensure reliable and secure data processing.",
            input
        );
    }

    private String buildSummary(String input) {
        return String.format(
            "%s is designed to streamline workflow, improve data integrity, " +
            "and deliver a reliable user experience across all operational scenarios.",
            input
        );
    }

    private List<String> extractComponents(String input) {
        String lower = input.toLowerCase();
        List<String> components = new ArrayList<>();
        if (lower.contains("user") || lower.contains("auth")) components.add("Authentication Module");
        if (lower.contains("data") || lower.contains("flow")) components.add("Data Flow Engine");
        if (lower.contains("api") || lower.contains("rest")) components.add("REST API Layer");
        if (lower.contains("db") || lower.contains("database")) components.add("Database Repository");
        if (components.isEmpty()) {
            components.addAll(List.of("Input Handler", "Processing Core", "Output Formatter"));
        }
        return components;
    }

    private String assessComplexity(String input) {
        int wordCount = input.trim().split("\\s+").length;
        if (wordCount <= 2) return "Low";
        if (wordCount <= 5) return "Medium";
        return "High";
    }
}
