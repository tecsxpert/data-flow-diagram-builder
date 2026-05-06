package com.internship.tool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.internship.tool.entity.Diagram;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface DiagramRepository extends JpaRepository<Diagram, Long> {

    List<Diagram> findByNameContainingIgnoreCase(String name);

    List<Diagram> findByRole(String role);

    @Query("SELECT d FROM Diagram d WHERE d.description IS NOT NULL AND d.description != ''")
    List<Diagram> findDiagramsWithDescription();

    List<Diagram> findByDeadlineBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT DISTINCT d.userEmail FROM Diagram d WHERE d.userEmail IS NOT NULL AND d.userEmail != ''")
    List<String> findDistinctUserEmails();

    List<Diagram> findByUserEmail(String userEmail);

    boolean existsByName(String name);
}