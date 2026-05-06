package com.internship.tool.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @Operation(summary = "Upload a file")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            if (file.getSize() > 2 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File too large (max 2MB)");
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            File dest = new File(UPLOAD_DIR + fileName);
            dest.getParentFile().mkdirs();

            file.transferTo(dest);

            return ResponseEntity.ok("File uploaded successfully");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Upload failed");
        }
    }
}