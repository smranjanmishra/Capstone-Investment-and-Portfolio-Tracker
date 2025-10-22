package com.mycom.zeta.investment_portfolio_Tracker.repository;

import com.mycom.zeta.investment_portfolio_Tracker.entities.Ticket;
import com.mycom.zeta.investment_portfolio_Tracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    // find the ticket by ticket ID
    Optional<Ticket> findTicketsById(Integer ticketId);

    //List all the tickets by User
    List<Ticket> findByUserId(Integer userId);
}
