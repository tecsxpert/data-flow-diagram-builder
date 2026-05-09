package com.internship.tool.dto;

import jakarta.validation.constraints.NotBlank;

public class AiRequest {

    @NotBlank(message = "Input is required")
    private String input;

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
}
