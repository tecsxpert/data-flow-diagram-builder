package com.internship.tool.service;

import com.internship.tool.dto.DiagramDTO;
import com.internship.tool.entity.Diagram;
import com.internship.tool.exception.InvalidInputException;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.DiagramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiagramServiceImplTest {

    @Mock
    private DiagramRepository repository;

    @InjectMocks
    private DiagramServiceImpl diagramService;

    private Diagram diagram;
    private DiagramDTO diagramDTO;

    @BeforeEach
    void setUp() {
        diagram = Diagram.builder()
                .id(1L)
                .name("Architecture Diagram")
                .description("Detailed cloud architecture")
                .build();

        diagramDTO = new DiagramDTO();
        diagramDTO.setName("New Diagram");
        diagramDTO.setDescription("New description");
    }

    // 1. Test saveDiagram (Success)
    @Test
    void testSaveDiagram_Success() {
        when(repository.save(any(Diagram.class))).thenReturn(diagram);

        Diagram savedDiagram = diagramService.saveDiagram(diagram);

        assertNotNull(savedDiagram);
        assertEquals("Architecture Diagram", savedDiagram.getName());
        verify(repository, times(1)).save(diagram);
    }

    // 2. Test getAllDiagrams (Success)
    @Test
    void testGetAllDiagrams_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Diagram> page = new PageImpl<>(Collections.singletonList(diagram));
        when(repository.findAll(pageable)).thenReturn(page);

        Page<Diagram> result = diagramService.getAllDiagrams(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Architecture Diagram", result.getContent().get(0).getName());
        verify(repository, times(1)).findAll(pageable);
    }

    // 3. Test getDiagramById (Found)
    @Test
    void testGetDiagramById_Found() {
        when(repository.findById(1L)).thenReturn(Optional.of(diagram));

        Diagram foundDiagram = diagramService.getDiagramById(1L);

        assertNotNull(foundDiagram);
        assertEquals(1L, foundDiagram.getId());
        verify(repository, times(1)).findById(1L);
    }

    // 4. Test getDiagramById (Not Found)
    @Test
    void testGetDiagramById_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            diagramService.getDiagramById(99L);
        });
        verify(repository, times(1)).findById(99L);
    }

    // 5. Test deleteDiagram (Success)
    @Test
    void testDeleteDiagram_Success() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        diagramService.deleteDiagram(1L);

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    // 6. Test deleteDiagram (Not Found)
    @Test
    void testDeleteDiagram_NotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            diagramService.deleteDiagram(99L);
        });

        verify(repository, times(1)).existsById(99L);
        verify(repository, never()).deleteById(anyLong());
    }

    // 7. Test updateDiagram (Success)
    @Test
    void testUpdateDiagram_Success() {
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(any(Diagram.class))).thenReturn(diagram);

        Diagram updatedDiagram = diagramService.updateDiagram(diagram);

        assertNotNull(updatedDiagram);
        assertEquals("Architecture Diagram", updatedDiagram.getName());
        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).save(diagram);
    }

    // 8. Test updateDiagram (Not Found)
    @Test
    void testUpdateDiagram_NotFound() {
        Diagram unknownDiagram = Diagram.builder().id(99L).name("Unknown").build();
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            diagramService.updateDiagram(unknownDiagram);
        });

        verify(repository, times(1)).existsById(99L);
        verify(repository, never()).save(any(Diagram.class));
    }

    // 9. Test createDiagram (Success)
    @Test
    void testCreateDiagram_Success() {
        when(repository.existsByName("New Diagram")).thenReturn(false);
        
        Diagram expectedDiagram = Diagram.builder()
                .id(2L)
                .name("New Diagram")
                .description("New description")
                .build();
                
        when(repository.save(any(Diagram.class))).thenReturn(expectedDiagram);

        Diagram createdDiagram = diagramService.createDiagram(diagramDTO);

        assertNotNull(createdDiagram);
        assertEquals("New Diagram", createdDiagram.getName());
        verify(repository, times(1)).existsByName("New Diagram");
        verify(repository, times(1)).save(any(Diagram.class));
    }

    // 10. Test createDiagram (Duplicate Name throws InvalidInputException)
    @Test
    void testCreateDiagram_DuplicateName() {
        when(repository.existsByName("New Diagram")).thenReturn(true);

        assertThrows(InvalidInputException.class, () -> {
            diagramService.createDiagram(diagramDTO);
        });

        verify(repository, times(1)).existsByName("New Diagram");
        verify(repository, never()).save(any(Diagram.class));
    }
}
