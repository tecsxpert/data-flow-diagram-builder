package com.internship.tool.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.internship.tool.entity.Diagram;
import com.internship.tool.repository.DiagramRepository;

@Service
public class DiagramService {

    @Autowired
    private DiagramRepository repo;

    public Diagram create(Diagram d) {
        return repo.save(d);
    }

    public List<Diagram> getAll() {
        return repo.findAll();
    }
}