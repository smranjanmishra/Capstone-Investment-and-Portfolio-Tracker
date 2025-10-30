package com.zeta.backend.repository;

import com.zeta.backend.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    // Finds all tickets raised by a specific userId and returns a list of Ticket objects belonging to that user.
    List<Ticket> findByUserId(Integer userId);
}
