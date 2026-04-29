package com.internship.tool.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.internship.tool.entity.Diagram;
import com.internship.tool.service.DiagramService;

@RestController
@RequestMapping("/api/diagram")
public class DiagramController {

    @Autowired
    private DiagramService service;

    // ✅ ROOT CHECK API
    @GetMapping("/")
    public String home() {
        return "Backend Connected Successfully 🚀";
    }

    @PostMapping("/create")
    public Diagram create(@RequestBody Diagram d) {
        return service.create(d);
    }

    @GetMapping("/all")
    public List<Diagram> getAll() {
        return service.getAll();
    }
}