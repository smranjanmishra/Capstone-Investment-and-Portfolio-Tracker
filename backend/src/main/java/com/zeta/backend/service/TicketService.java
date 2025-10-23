package com.zeta.backend.service;

import com.zeta.backend.models.Ticket;
import com.zeta.backend.models.User;
import com.zeta.backend.enums.TicketPriority;
import com.zeta.backend.enums.TicketStatus;
import com.zeta.backend.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    public Ticket createTicket(Ticket ticket) {
        String ticketDescription = ticket.getSubject().toLowerCase();
        logger.info("Creating ticket with subject: {}", ticket.getSubject());

        if (ticketDescription.contains("urgent") || ticketDescription.contains("immediately")) {
            ticket.setPriority(TicketPriority.HIGH);
            logger.info("Ticket priority set to HIGH");
        } else if (ticketDescription.contains("soon") || ticketDescription.contains("problem")) {
            ticket.setPriority(TicketPriority.MEDIUM);
            logger.info("Ticket priority set to MEDIUM");
        } else {
            ticket.setPriority(TicketPriority.LOW);
            logger.info("Ticket priority set to LOW");
        }

        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus(TicketStatus.OPEN);
        Ticket savedTicket = ticketRepository.save(ticket);
        logger.info("Ticket saved successfully with ID: {}", savedTicket.getId());
        return savedTicket;
    }

    public List<Ticket> getAllTickets() {
        logger.info("Fetching all tickets");
        List<Ticket> tickets = ticketRepository.findAll();
        logger.info("Total tickets fetched: {}", tickets.size());
        return tickets;
    }

    public Optional<Ticket> getTicketById(Integer ticketId) {
        logger.info("Fetching ticket with ID: {}", ticketId);
        return ticketRepository.findById(ticketId);
    }

    public List<Ticket> getTicketsByUser(User user) {
        logger.info("Fetching tickets for userId: {}", user.getId());
        List<Ticket> tickets = ticketRepository.findByUserId(user.getId());
        logger.info("Total tickets found for userId {}: {}", user.getId(), tickets.size());
        return tickets;
    }

    public Ticket updateTicket(Integer ticketId, TicketStatus newStatus, String response) {
        logger.info("Updating ticketId: {} with status: {}", ticketId, newStatus);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    logger.warn("Ticket not found with ID: {}", ticketId);
                    return new RuntimeException("Ticket not found");
                });

        ticket.setStatus(newStatus);
        ticket.setResponse(response);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);
        logger.info("Ticket updated successfully with ID: {}", updatedTicket.getId());
        return updatedTicket;
    }
}
