package com.zeta.backend.service;

import com.zeta.backend.models.Ticket;
import com.zeta.backend.models.User;
import com.zeta.backend.enums.TicketPriority;
import com.zeta.backend.enums.TicketStatus;
import com.zeta.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    public Ticket createTicket(Ticket ticket) {
        String ticketDescription = ticket.getSubject().toLowerCase();

        if (ticketDescription.contains("urgent") || ticketDescription.contains("immediately")) {
            ticket.setTicketPriority(TicketPriority.HIGH);
        } else if (ticketDescription.contains("soon") || ticketDescription.contains("problem")) {
            ticket.setTicketPriority(TicketPriority.MEDIUM);
        } else {
            ticket.setTicketPriority(TicketPriority.LOW);
        }

        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setTicketStatus(TicketStatus.OPEN);

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Integer ticketId) {
        return ticketRepository.findById(ticketId);
    }

    public List<Ticket> getTicketsByUser(User user) {
        return ticketRepository.findByUserId(user.getId());
    }

    public Ticket updateTicket(Integer ticketId, TicketStatus newStatus, String response) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setTicketStatus(newStatus);
        ticket.setResponse(response);
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }
}
