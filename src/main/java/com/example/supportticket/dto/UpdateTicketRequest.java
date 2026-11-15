package com.example.supportticket.dto;

import com.example.supportticket.entity.*;
import jakarta.validation.constraints.*;

public record UpdateTicketRequest(@NotBlank @Size(max = 150) String title,
                                  @NotBlank @Size(max = 4000) String description,
                                  @NotNull Priority priority,
                                  @NotNull TicketStatus status)
{
}
