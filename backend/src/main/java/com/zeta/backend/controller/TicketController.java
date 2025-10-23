package com.zeta.backend.controller;

import com.zeta.backend.dto.RespondDto;
import com.zeta.backend.dto.TicketResponseDto;
import com.zeta.backend.enums.TicketStatus;
import com.zeta.backend.models.Ticket;
import com.zeta.backend.models.User;
import com.zeta.backend.service.PortfolioServiceImpl;
import com.zeta.backend.service.TicketService;
import com.zeta.backend.service.UserService;
import com.zeta.backend.util.TicketDtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/support")
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @Autowired
    private PortfolioServiceImpl portfolioService;

    //  Helper function to safely extract userId
    private Long extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Integer) return ((Integer) principal).longValue();
        if (principal instanceof Long) return (Long) principal;
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    // create a ticket (user only)
    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody Ticket ticket, Authentication authentication) {
        try {
            Long userId = extractUserId(authentication);
            logger.info("Create ticket request received from userId: {}", userId);

            Optional<User> loggedInUseroptional = userService.getUserById(userId);
            if (loggedInUseroptional.isEmpty()) {
                logger.warn("User not found for UserId: {}", userId);
                return ResponseEntity.status(403).body("User not found");
            }
            User loggedInUser = loggedInUseroptional.get();

            if (ticket.getInvestmentProductId() != null) {
                boolean ownsInvestment = portfolioService.getPortfolioByUser(userId)
                        .stream()
                        .anyMatch(p -> p.getInvestmentProductId().equals(ticket.getInvestmentProductId()));

                if (!ownsInvestment) {
                    logger.warn("User {} does not own investment id {}", userId, ticket.getInvestmentProductId());
                    return ResponseEntity.badRequest().body("Investment ID does not exist for the user");
                }
            }

            ticket.setUserId(loggedInUser.getId());
            Ticket createdTicket = ticketService.createTicket(ticket);
            TicketResponseDto ticketResponseDto = TicketDtoMapper.mapTicketToDto(createdTicket, loggedInUser);

            logger.info("Ticket created successfully with ID: {} by userId: {}", ticketResponseDto.getId(), userId);
            return ResponseEntity.ok("Ticket created successfully with ID: " + ticketResponseDto.getId());
        } catch (Exception e) {
            logger.error("Failed to create ticket", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // get tickets by logged-in user
    @GetMapping("/user")
    public ResponseEntity<?> getTicketByUser(Authentication authentication) {
        try {
            Long userId = extractUserId(authentication);
            logger.info("Get tickets request received from userId: {}", userId);

            Optional<User> loggedInUseroptional = userService.getUserById(userId);
            if (loggedInUseroptional.isEmpty()) {
                logger.warn("User not found for userId: {}", userId);
                return ResponseEntity.status(403).body("User not found");
            }
            User loggedInUser = loggedInUseroptional.get();

            List<Ticket> ticketListOfUser = ticketService.getTicketsByUser(loggedInUser);
            List<TicketResponseDto> listOfTicketDto = ticketListOfUser.stream()
                    .map(ticket -> TicketDtoMapper.mapTicketToDto(ticket, loggedInUser))
                    .toList();

            logger.info("Returning {} tickets for userId: {}", listOfTicketDto.size(), userId);
            return ResponseEntity.ok(listOfTicketDto);
        } catch (Exception e) {
            logger.error("Failed to get tickets for user", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // respond to ticket (admin only)
    @PutMapping("/{ticketId}/respond")
    public ResponseEntity<?> updateTicketByAdmin(@PathVariable Integer ticketId,
                                                 @RequestBody RespondDto responseDto,
                                                 Authentication authentication) {
        try {
            Long userId = extractUserId(authentication);
            logger.info("Admin request to respond to ticketId: {} by userId: {}", ticketId, userId);

            User loggedInUser = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!userService.isAdmin(loggedInUser)) {
                logger.warn("Non-admin user attempted to respond to ticket: {}", userId);
                return ResponseEntity.status(403).body("Access denied only admin can respond to ticket");
            }

            Optional<Ticket> userTicketOptional = ticketService.getTicketById(ticketId);
            if (userTicketOptional.isEmpty()) {
                logger.warn("Ticket not found with ticketId: {}", ticketId);
                return ResponseEntity.badRequest().body("No Ticket found");
            }

            Ticket userTicket = userTicketOptional.get();
            userTicket.setResponse(responseDto.getResponse());
            userTicket.setStatus(TicketStatus.RESPONDED);
            ticketService.updateTicket(userTicket.getId(), TicketStatus.RESPONDED, responseDto.getResponse());

            TicketResponseDto userTicketDto = TicketDtoMapper.mapTicketToDto(userTicket, loggedInUser);
            logger.info("Ticket updated successfully with ID: {} by admin userId: {}", userTicketDto.getId(), userId);
            return ResponseEntity.ok("Ticket updated successfully " + userTicketDto.getId());
        } catch (Exception e) {
            logger.error("Failed to update ticket", e);
            return ResponseEntity.badRequest().body("Failed to update Ticket " + e.getMessage());
        }
    }
}
