package com.zeta.backend.repository;

import com.zeta.backend.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    // Find a ticket by its unique ticket ID.
    // ticketId -> the ID of the ticket to search for
    // return Optional containing the Ticket if found
    Optional<Ticket> findTicketsById(Integer ticketId);

    // Find all tickets raised by a specific user.
    // userId -> ID of the user whose tickets are to be fetched
    // return List of Ticket objects belonging to that user
    List<Ticket> findByUserId(Integer userId);
}
