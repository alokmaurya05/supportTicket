package com.example.supportticket.dto;

import com.example.supportticket.entity.Ticket;

import java.time.Instant;

public record TicketResponse(Long id, String title, String description, String priority, String status,
                             String createdBy, Instant createdAt, Instant updatedAt) {
    public static TicketResponse from(Ticket t) {
        return new TicketResponse(t.getId(), t.getTitle(), t.getDescription(), t.getPriority().name(), t.getStatus().name(), t.getCreatedBy().getUsername(), t.getCreatedAt(), t.getUpdatedAt());
    }
}
