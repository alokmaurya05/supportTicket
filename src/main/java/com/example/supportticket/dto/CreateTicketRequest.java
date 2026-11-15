package com.example.supportticket.dto;

import com.example.supportticket.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(@NotBlank @Size(max = 150) String title,
                                  @NotBlank @Size(max = 4000) String description,
                                  @NotNull Priority priority) {
}
