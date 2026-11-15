package com.example.supportticket.controller;

import com.example.supportticket.dto.CreateTicketRequest;
import com.example.supportticket.dto.TicketResponse;
import com.example.supportticket.dto.UpdateTicketRequest;
import com.example.supportticket.service.TicketService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService tickets;

    public TicketController(TicketService tickets) {
        this.tickets = tickets;
    }
    private static final Logger log = LoggerFactory.getLogger(TicketController.class);
    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tickets.create(request, auth.getName()));
    }
    @GetMapping("/mytickets")
    public List<TicketResponse> listAllTicketsByUser(Authentication auth) {
        return tickets.findAllTicketByCreator(auth.getName());
    }
    @GetMapping
    public List<TicketResponse> list() {
        return tickets.findAll();
    }

    @GetMapping("/{id}")
    public TicketResponse get(@PathVariable Long id) {
        return tickets.findById(id);
    }

    @PutMapping("/{id}")
    public TicketResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTicketRequest request, Authentication auth) {
        log.info("Updating ticket id={} by user={}", id, auth.getName());
        return tickets.update(id, request, auth.getName());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        log.info("Deleting ticket id={} by user={}", id, auth.getName());
        tickets.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
