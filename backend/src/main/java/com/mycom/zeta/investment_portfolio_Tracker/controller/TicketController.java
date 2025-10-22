package com.mycom.zeta.investment_portfolio_Tracker.controller;
import com.mycom.zeta.investment_portfolio_Tracker.dto.RespondDto;
import com.mycom.zeta.investment_portfolio_Tracker.dto.TicketResponseDto;
import com.mycom.zeta.investment_portfolio_Tracker.dto.UserResponseDto;
import com.mycom.zeta.investment_portfolio_Tracker.entities.Ticket;
import com.mycom.zeta.investment_portfolio_Tracker.entities.User;
import com.mycom.zeta.investment_portfolio_Tracker.enums.TicketStatus;
import com.mycom.zeta.investment_portfolio_Tracker.services.TicketService;
import com.mycom.zeta.investment_portfolio_Tracker.services.UserService;
import com.mycom.zeta.investment_portfolio_Tracker.utils.TicketDtoMapper;
import com.mycom.zeta.investment_portfolio_Tracker.utils.UserDtoMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/support")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    //create a ticket by user only authenticated user can create a ticket

    @PostMapping
    public ResponseEntity<?>createTicket(@RequestBody Ticket ticket, HttpServletRequest httpServletRequest){
        try{
            User loggedInUser=userService.getLoggedInUser(httpServletRequest,true);

            if (userService.isAdmin(loggedInUser)) {
                return ResponseEntity.status(403).body("Admins are not allowed to create support tickets.");
            }
            ticket.setUserId(loggedInUser.getId());
            Ticket createdTicket=ticketService.createTicket(ticket);
            TicketResponseDto ticketResponseDto= TicketDtoMapper.mapTicketToDto(createdTicket,loggedInUser);
            return ResponseEntity.ok("Ticket created successfully with ID: " + ticketResponseDto.getId());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user")
// get all tickets by user
    public ResponseEntity<?>getTicketByUser(HttpServletRequest request){

        try{
            User loggedInUser=userService.getLoggedInUser(request,true);
            UserResponseDto userResponseDto= UserDtoMapper.UserMapperToDto(loggedInUser);
            List<Ticket> ticketListOfUser=ticketService.getTicketsByUser(loggedInUser);
            List<TicketResponseDto>listOfTicketDto=ticketListOfUser.stream().map(ticket -> TicketDtoMapper.mapTicketToDto(ticket,loggedInUser)).toList();
            return ResponseEntity.ok(listOfTicketDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/admin/support")
    //get all tickets (only by admin)

    public ResponseEntity<?>getTicketsByAdmin(HttpServletRequest request){
        try{
            User loggedInUser=userService.getLoggedInUser(request,true);
            if(!userService.isAdmin(loggedInUser)){
                return ResponseEntity.badRequest().body("User not authorised to complete the request");
            }
            List<Ticket>allTickets=ticketService.getAllTickets();
           List<TicketResponseDto>listOfAllTicketDto=allTickets.stream().map(ticket -> {
              User ticketOwner=null;
              if(ticket.getUserId()!=null){
                  ticketOwner=userService.getUserById(ticket.getUserId()).orElse(null);
              }
              return TicketDtoMapper.mapTicketToDto(ticket,ticketOwner);
           }).toList();
            return ResponseEntity.ok(listOfAllTicketDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @PutMapping("/{ticketId}/respond")
    public ResponseEntity<?>updateTicketByAdmin(HttpServletRequest request, @PathVariable Integer ticketId, @RequestBody RespondDto responseDto){
        try{
            User loggedInUser=userService.getLoggedInUser(request,true);
            if(!userService.isAdmin(loggedInUser)){
                return ResponseEntity.status(403).body("Access denied only admin can respond to ticket");

            }
            Optional<Ticket> userTicketOptional=ticketService.getTicketById(ticketId);
            if(userTicketOptional.isEmpty()){
                return ResponseEntity.badRequest().body("No Ticket found");

            }
            Ticket userTicket=userTicketOptional.get();
            userTicket.setResponse(responseDto.getResponse());
            userTicket.setTicketStatus(TicketStatus.RESPONDED);
            ticketService.updateTicket(userTicket.getId(),TicketStatus.RESPONDED,responseDto.getResponse());
            TicketResponseDto userTicketDto=TicketDtoMapper.mapTicketToDto(userTicket,loggedInUser);
            return ResponseEntity.ok("Ticket updated successfully "+ userTicketDto.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update Ticket "+e.getMessage());

        }

    }


}
