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


    // Create a new support ticket.
    // Automatically sets ticket priority based on subject keywords.
    // Sets ticket status to OPEN and createdAt timestamp to current time.
    // ticket -> Ticket object containing subject, description, and optional investment info
    // returns -> Saved Ticket object with generated ID and timestamps
    public Ticket createTicket(Ticket ticket) {
        String ticketDescription = ticket.getSubject().toLowerCase();
        logger.info("Creating ticket with subject: {}", ticket.getSubject());

        if (ticketDescription.toLowerCase().contains("urgent") || ticketDescription.toLowerCase().contains("immediately")|| ticketDescription.toLowerCase().contains("delay")) {
            ticket.setPriority(TicketPriority.HIGH);
            logger.info("Ticket priority set to HIGH");
        } else if (ticketDescription.toLowerCase().contains("soon") || ticketDescription.toLowerCase().contains("problem")||ticketDescription.toLowerCase().contains("slow")) {
            ticket.setPriority(TicketPriority.LOW);
            logger.info("Ticket priority set to LOW");
        } else {
            ticket.setPriority(TicketPriority.MEDIUM);
            logger.info("Ticket priority set to MEDIUM");
        }


        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus(TicketStatus.OPEN);
        Ticket savedTicket = ticketRepository.save(ticket);
        logger.info("Ticket saved successfully with ID: {}", savedTicket.getId());
        return savedTicket;
    }

    // Retrieve all tickets in the system (admin usage)
    // returns -> List of all Ticket objects
    public List<Ticket> getAllTickets() {
        logger.info("Fetching all tickets");
        List<Ticket> tickets = ticketRepository.findAll();
        logger.info("Total tickets fetched: {}", tickets.size());
        return tickets;
    }

    // Retrieve a ticket by its unique ticket ID
    // ticketId -> ID of the ticket to fetch
    // returns -> Optional containing the Ticket if found
    public Optional<Ticket> getTicketById(Integer ticketId) {
        logger.info("Fetching ticket with ID: {}", ticketId);
        return ticketRepository.findById(ticketId);
    }
    // Retrieve all tickets for a specific user
    // user -> User object whose tickets need to be fetched
    // returns -> List of Ticket objects belonging to the user
    public List<Ticket> getTicketsByUser(User user) {
        logger.info("Fetching tickets for userId: {}", user.getId());
        List<Ticket> tickets = ticketRepository.findByUserId(Math.toIntExact(user.getId()));
        logger.info("Total tickets found for userId {}: {}", user.getId(), tickets.size());
        return tickets;
    }
    // Update a ticket's status and admin response
    // ticketId -> ID of the ticket to update
    // newStatus -> New status to set (e.g., RESPONDED, CLOSED)
    // response -> Admin response text
    // returns -> Updated Ticket object
    public Ticket updateTicket(Integer ticketId, TicketStatus newStatus, String response, TicketPriority newPriority) {
        logger.info("Updating ticketId: {} with status: {}, priority: {}", ticketId, newStatus, newPriority == null ? "(unchanged)" : newPriority); // Updated log
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    logger.warn("Ticket not found with ID: {}", ticketId);
                    return new RuntimeException("Ticket not found");
                });
        ticket.setStatus(newStatus);
        if (response != null) {
            ticket.setResponse(response);
        }
        if (newPriority != null) {
            ticket.setPriority(newPriority);
            logger.info("Priority for ticketId: {} set to {}", ticketId, newPriority);
        }
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updatedTicket = ticketRepository.save(ticket);
        logger.info("Ticket updated successfully with ID: {}", updatedTicket.getId());
        return updatedTicket;
    }
}
