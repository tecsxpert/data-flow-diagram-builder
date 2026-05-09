package com.internship.tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.dto.DiagramDTO;
import com.internship.tool.entity.Diagram;
import com.internship.tool.service.DiagramService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class DiagramControllerTest {

    @Autowired
    private DiagramController diagramController;

    private MockMvc mockMvc;

    @MockitoBean
    private DiagramService diagramService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(diagramController).build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateDiagram_Success() throws Exception {
        DiagramDTO dto = new DiagramDTO();
        dto.setName("Test Diagram");
        dto.setDescription("Description");

        Diagram diagram = new Diagram();
        diagram.setId(1L);
        diagram.setName("Test Diagram");

        when(diagramService.createDiagram(any(DiagramDTO.class))).thenReturn(diagram);

        mockMvc.perform(post("/diagrams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Diagram"));
    }
}
