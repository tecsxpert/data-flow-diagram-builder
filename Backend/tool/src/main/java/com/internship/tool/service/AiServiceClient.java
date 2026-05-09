package com.internship.tool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Bridges DfdRecordService → AiService (fully in-process, no HTTP call needed).
 */
@Service
public class AiServiceClient {

    @Autowired
    private AiService aiService;

    public String getAiResponse(String input) {
        try {
            Map<String, Object> result = aiService.describe(input);
            Object description = result.get("description");
            return description != null ? description.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}