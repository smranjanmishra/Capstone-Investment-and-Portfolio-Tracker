package com.mycom.zeta.investment_portfolio_Tracker.services;

import com.mycom.zeta.investment_portfolio_Tracker.entities.Ticket;
import com.mycom.zeta.investment_portfolio_Tracker.entities.User;
import com.mycom.zeta.investment_portfolio_Tracker.enums.TicketPriority;
import com.mycom.zeta.investment_portfolio_Tracker.enums.TicketStatus;
import com.mycom.zeta.investment_portfolio_Tracker.repository.TicketRepository;
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


    public Ticket createTicket(Ticket ticket){
        String  ticketDescription= ticket.getSubject().toLowerCase();
        if(ticketDescription.contains("urgent")||ticketDescription.contains("immediately")){
            ticket.setTicketPriority(TicketPriority.HIGH);
        }
        else if(ticketDescription.contains("soon")||ticketDescription.contains("problem")){
            ticket.setTicketPriority(TicketPriority.MEDIUM);
        }
        else{
            ticket.setTicketPriority(TicketPriority.LOW);
        }
        ticket.setUserId(ticket.getUserId());
        ticket.setInvestmentId(ticket.getInvestmentId());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setTicketStatus(TicketStatus.OPEN);
        ticketRepository.save(ticket);
        return ticket;
    }


    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }


    public Optional<Ticket> getTicketById(Integer ticketId) {
        return ticketRepository.findTicketsById(ticketId);
    }


    public List<Ticket> getTicketsByUser(User user) {
        return ticketRepository.findByUserId(user.getId());
    }



    //method to update ticket  (only by admin)

    public Ticket updateTicket(Integer ticketId, TicketStatus newStatus,String response) {
        Optional<Ticket>optionalTicket=ticketRepository.findTicketsById(ticketId);
        if(optionalTicket.isEmpty()){
            throw new RuntimeException("Ticket not found");

        }
        Ticket ticket=optionalTicket.get();
        ticket.setTicketStatus(newStatus);
        ticket.setResponse(response);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        return ticket;
    }


}
