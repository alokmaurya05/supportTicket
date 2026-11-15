package com.example.supportticket.service;

import com.example.supportticket.dto.*;
import com.example.supportticket.entity.*;
import com.example.supportticket.exception.ResourceNotFoundException;
import com.example.supportticket.repository.*;
import org.slf4j.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TicketService {
    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository tickets;
    private final UserRepository users;

    public TicketService(TicketRepository tickets, UserRepository users) {
        this.tickets = tickets;
        this.users = users;
    }

    public TicketResponse create(CreateTicketRequest request, String username) {
        AppUser creator = users.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        Ticket ticket = tickets.save(new Ticket(request.title(), request.description(), request.priority(), creator));
        log.info("Created ticket id={} by={}", ticket.getId(), username);
        return TicketResponse.from(ticket);
    }
    @Transactional(readOnly = true)
    public List<TicketResponse> findAllTicketByCreator(String username) {
        AppUser creator = users.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        log.info("Finding all tickets by user={}", username);
        List<Ticket> ticketsList = tickets.findByCreatedById(creator.getId());
        log.info("Found {} tickets for user={}", ticketsList.size(), username);
        return ticketsList.stream().map(TicketResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> findAll() {
        return tickets.findAll().stream().map(TicketResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse findById(Long id) {
        return TicketResponse.from(findTicket(id));
    }

    public TicketResponse update(Long id, UpdateTicketRequest request, String username) {
        Ticket ticket = findTicket(id);
        ticket.update(request.title(), request.description(), request.priority(), request.status());
        Ticket saved = tickets.save(ticket);
        log.info("Updated ticket id={} by={} status={}", id, username, saved.getStatus());
        return TicketResponse.from(saved);
    }

    public void delete(Long id, String username) {
        tickets.delete(findTicket(id));
        log.warn("Deleted ticket id={} by={}", id, username);
    }

    private Ticket findTicket(Long id) {
        return tickets.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + id + " was not found"));
    }
}
