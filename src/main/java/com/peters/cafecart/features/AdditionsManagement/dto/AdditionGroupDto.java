package com.peters.cafecart.features.AdditionsManagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AdditionGroupDto {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Max selectable is required")
    @Min(value = 1, message = "Max selectable must be at least 1")
    private Integer maxSelectable;

    private List<AdditionDto> additions;
}
