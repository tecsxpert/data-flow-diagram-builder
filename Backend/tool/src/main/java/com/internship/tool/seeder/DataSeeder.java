package com.internship.tool.seeder;

import com.internship.tool.entity.Diagram;
import com.internship.tool.repository.DiagramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DiagramRepository diagramRepository;

    @Override
    public void run(String... args) throws Exception {
        if (diagramRepository.count() < 30) {
            seedDiagrams();
        }
    }

    private void seedDiagrams() {
        List<Diagram> diagrams = new ArrayList<>();
        long currentCount = diagramRepository.count();
        int toSeed = (int) (30 - currentCount);

        for (int i = 1; i <= toSeed; i++) {
            Diagram diagram = new Diagram();
            diagram.setName("Sample Diagram " + (currentCount + i));
            diagram.setDescription("This is an automatically seeded sample diagram for testing purposes.");
            diagram.setUserEmail("user@example.com");
            diagram.setRole("ROLE_USER");
            diagram.setDeadline(LocalDateTime.now().plusDays(7 + i));
            diagrams.add(diagram);
        }

        diagramRepository.saveAll(diagrams);
    }
}
