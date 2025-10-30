package com.zeta.backend.controller;

import com.zeta.backend.dto.RespondDto;
import com.zeta.backend.dto.TicketResponseDto;
import com.zeta.backend.enums.TicketPriority;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    // helper: extract userId from Authentication principal
    // handles different principal types (Integer / Long)
    private Long extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof Long)
            return (Long) principal;
        if (principal instanceof Integer)
            return ((Integer) principal).longValue();
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    // create a new support ticket (User only)
    // POST /api/v1/support
    // requires: valid JWT token
    // validates: user owns the investment (if provided)
    // response: 200 OK on success with ticket ID or error message
    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody Ticket ticket, Authentication authentication) {
        try {
            Long userId = extractUserId(authentication);
            logger.info("Create ticket request received from userId: {}", userId);

            // Validate investment ownership if investmentProductId is provided
            if (ticket.getInvestmentProductId() != null) {
                boolean ownsInvestment = portfolioService.getPortfolioByUser(userId)
                        .stream()
                        .anyMatch(p -> p.getInvestmentProductId().equals(ticket.getInvestmentProductId()));

                if (!ownsInvestment) {
                    logger.warn("User {} does not own investment id {}", userId, ticket.getInvestmentProductId());
                    return ResponseEntity.badRequest().body("Investment ID does not exist for the user");
                }
            }

            // Set userId directly from JWT authentication and create ticket
            ticket.setUserId(userId);
            Ticket createdTicket = ticketService.createTicket(ticket);

            logger.info("Ticket created successfully with ID: {} by userId: {}", createdTicket.getId(), userId);
            return ResponseEntity.ok("Ticket created successfully with ID: " + createdTicket.getId());
        } catch (Exception e) {
            logger.error("Failed to create ticket", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // get all tickets for the authenticated user
    // GET /api/v1/support/user
    // requires: valid JWT token
    // response: 200 OK with list of user's tickets
    @GetMapping("/user")
    public ResponseEntity<?> getTicketByUser(Authentication authentication) {
        try {
            Long userId = extractUserId(authentication);
            logger.info("Get tickets request received from userId: {}", userId);

            // Fetch tickets directly by userId - no need to verify user exists (JWT guarantees it)
            List<Ticket> ticketListOfUser = ticketService.getTicketsByUserId(userId);
            
            // Map tickets to DTOs without user object (user info not critical for this endpoint)
            List<TicketResponseDto> listOfTicketDto = ticketListOfUser.stream()
                    .map(ticket -> TicketDtoMapper.mapTicketToDto(ticket, null))
                    .toList();

            logger.info("Returning {} tickets for userId: {}", listOfTicketDto.size(), userId);
            return ResponseEntity.ok(listOfTicketDto);
        } catch (Exception e) {
            logger.error("Failed to get tickets for user", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // respond to a ticket (Admin only)
    // PUT /api/v1/support/{ticketId}/respond
    // requires: valid JWT token for admin user
    // validates: admin privileges and existing ticket
    // response: 200 OK with confirmation or error message
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{ticketId}/respond")
    public ResponseEntity<?> updateTicketByAdmin(@PathVariable Integer ticketId,
            @RequestBody RespondDto responseDto,
            Authentication authentication) {
        try {
            Long userId = extractUserId(authentication);
            logger.info("Admin {} attempting to respond to ticketId: {}", userId, ticketId);

            Optional<Ticket> userTicketOptional = ticketService.getTicketById(ticketId);
            if (userTicketOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Ticket not found with ID: " + ticketId);
            }
            Ticket userTicket = userTicketOptional.get();

            String responseText = (responseDto != null) ? responseDto.getResponse() : null;
            TicketPriority newPriority = (responseDto != null) ? responseDto.getPriority() : null;
            if (responseText == null || responseText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Response text is required to respond to or close a ticket.");
            }
            Ticket updatedOrClosedTicketResult;
            TicketStatus currentStatus = userTicket.getStatus();
            if (currentStatus == TicketStatus.OPEN) {
                logger.info("Admin {} responding to OPEN ticketId: {}. Setting priority: {}", userId, ticketId,
                        newPriority);

                updatedOrClosedTicketResult = ticketService.updateTicket(ticketId, TicketStatus.RESPONDED, responseText,
                        newPriority);
                logger.info("Ticket {} responded successfully by admin {}", ticketId, userId);
            } else if (currentStatus == TicketStatus.RESPONDED) {
                logger.info("Admin {} closing RESPONDED ticketId: {} with final comment. Setting priority: {}", userId,
                        ticketId, newPriority);
                updatedOrClosedTicketResult = ticketService.updateTicket(ticketId, TicketStatus.CLOSED, responseText,
                        null);
                logger.info("Ticket {} closed successfully by admin {}", ticketId, userId);
            } else {
                logger.warn("Admin {} attempting action on already CLOSED ticket {}", userId, ticketId);
                return ResponseEntity.badRequest().body("Cannot modify a ticket that is already CLOSED.");
            }
            User ticketOwner = userService.getUserById(updatedOrClosedTicketResult.getUserId()).orElse(null);
            TicketResponseDto ticketResponseDto = TicketDtoMapper.mapTicketToDto(updatedOrClosedTicketResult,
                    ticketOwner);
            return ResponseEntity.ok(ticketResponseDto);
        } catch (Exception e) {
            logger.error("Failed action on ticket {} by admin", ticketId, e);
            return ResponseEntity.internalServerError().body("Failed action on ticket: " + e.getMessage());
        }
    }
}
