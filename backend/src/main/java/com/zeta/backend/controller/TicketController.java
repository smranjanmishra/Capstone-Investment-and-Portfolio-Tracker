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
        if (principal instanceof Integer) return ((Integer) principal).longValue();
        if (principal instanceof Long) return (Long) principal;
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    // create a new support ticket (User only)
    // POST /api/v1/support
    // requires: valid JWT token
    // validates: user exists and owns the investment (if provided)
    // response: 200 OK on success with ticket ID or error message
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

    // get all tickets for the authenticated user
    // GET /api/v1/support/user
    // requires: valid JWT token
    // response: 200 OK with list of user's tickets
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

    // respond to a ticket (Admin only)
    // PUT /api/v1/support/{ticketId}/respond
    // requires: valid JWT token for admin user
    // validates: admin privileges and existing ticket
    // response: 200 OK with confirmation or error message
    @PutMapping("/{ticketId}/respond")
    public ResponseEntity<?> updateTicketByAdmin(@PathVariable Integer ticketId,
                                                 @RequestBody RespondDto responseDto,
                                                 Authentication authentication) {
        try {
            Long userId = extractUserId(authentication);
            User loggedInUser = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!userService.isAdmin(loggedInUser)) {
                return ResponseEntity.status(403).body("Access denied: Admin privileges required.");
            }

            Optional<Ticket> userTicketOptional = ticketService.getTicketById(ticketId);
            if (userTicketOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Ticket not found with ID: " + ticketId);
            }
            Ticket userTicket = userTicketOptional.get();

            String responseText = (responseDto != null) ? responseDto.getResponse() : null;
            TicketPriority newPriority = (responseDto != null) ? responseDto.getPriority() : null; // <-- Get priority

            if (responseText == null || responseText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Response text is required to respond to or close a ticket.");
            }

            Ticket updatedOrClosedTicketResult;
            TicketStatus currentStatus = userTicket.getStatus();

            if (currentStatus == TicketStatus.OPEN) {
                logger.info("Admin {} responding to OPEN ticketId: {}. Setting priority: {}", userId, ticketId, newPriority);

                updatedOrClosedTicketResult = ticketService.updateTicket(ticketId, TicketStatus.RESPONDED, responseText, newPriority); // <-- Pass priority
                logger.info("Ticket {} responded successfully by admin {}", ticketId, userId);

            } else if (currentStatus == TicketStatus.RESPONDED) {
                logger.info("Admin {} closing RESPONDED ticketId: {} with final comment. Setting priority: {}", userId, ticketId, newPriority);

                updatedOrClosedTicketResult = ticketService.updateTicket(ticketId, TicketStatus.CLOSED, responseText, null);
                logger.info("Ticket {} closed successfully by admin {}", ticketId, userId);

            } else {
                logger.warn("Admin {} attempting action on already CLOSED ticket {}", userId, ticketId);
                return ResponseEntity.badRequest().body("Cannot modify a ticket that is already CLOSED.");
            }

            User ticketOwner = userService.getUserById(updatedOrClosedTicketResult.getUserId()).orElse(null);
            TicketResponseDto ticketResponseDto = TicketDtoMapper.mapTicketToDto(updatedOrClosedTicketResult, ticketOwner);
            return ResponseEntity.ok(ticketResponseDto);

        } catch (Exception e) {
            logger.error("Failed action on ticket {} by admin", ticketId, e);
            return ResponseEntity.internalServerError().body("Failed action on ticket: " + e.getMessage());
        }
    }
}
