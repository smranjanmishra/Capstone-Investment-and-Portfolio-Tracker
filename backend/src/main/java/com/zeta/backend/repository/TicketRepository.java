package com.zeta.backend.repository;

import com.zeta.backend.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    // Finds a ticket by its unique ticketId and returns an Optional containing the
    // Ticket if found.
    Optional<Ticket> findTicketsById(Integer ticketId);

    // Finds all tickets raised by a specific userId and returns a list of Ticket
    // objects belonging to that user.
    List<Ticket> findByUserId(Long userId);
}
