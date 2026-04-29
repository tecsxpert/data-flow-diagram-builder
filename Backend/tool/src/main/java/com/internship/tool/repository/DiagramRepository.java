package com.internship.tool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.internship.tool.entity.Diagram;

@Repository
public interface DiagramRepository extends JpaRepository<Diagram, Long> {

    // Custom methods (use later)
    List<Diagram> findByStatus(String status);

    List<Diagram> findByNameContainingIgnoreCase(String name);
}