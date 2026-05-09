package com.internship.tool.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;

import com.internship.tool.entity.Diagram;
import com.internship.tool.service.DiagramService;
import com.internship.tool.dto.DiagramDTO;

@RestController
@RequestMapping("/diagrams")
public class DiagramController {

    private final DiagramService service;

    public DiagramController(DiagramService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<Diagram>> getAll(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getAllDiagrams(PageRequest.of(page, size)));
    }
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Diagram> getDiagram(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDiagramById(id));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiagram(@PathVariable Long id) {
        service.deleteDiagram(id);
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<Diagram> createDiagram(@Valid @RequestBody DiagramDTO dto) {
        Diagram created = service.createDiagram(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}