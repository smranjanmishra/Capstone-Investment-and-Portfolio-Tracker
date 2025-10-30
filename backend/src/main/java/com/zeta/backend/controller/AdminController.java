package com.zeta.backend.controller;

import com.zeta.backend.dto.TicketResponseDto;
import com.zeta.backend.dto.UserResponse;
import com.zeta.backend.models.Ticket;
import com.zeta.backend.models.User;
import com.zeta.backend.service.TicketService;
import com.zeta.backend.service.UserService;
import com.zeta.backend.util.TicketDtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// controller for admin endpoints.
// handles admin-only operations (requires ADMIN role).

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;

    @Autowired
    private TicketService ticketService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

//     get all users (admin only).
//     GET /admin/users
//     requires: valid JWT token with ADMIN role
//     response: 200 OK with list of all users (excluding passwords)
//     return ResponseEntity with List of UserResponse
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        logger.info("Admin request to get all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get all support tickets (admin only).
    // Endpoint: GET /admin/support
    // Requires: Valid JWT token with ADMIN role.
    // Response: 200 OK with list of all tickets along with user info.
    // Returns: List<TicketResponseDto> mapped from Ticket entities.
    @GetMapping("/support")
    public ResponseEntity<?> getTicketsByAdmin(Authentication authentication) {
        try {

            Long userId = Long.parseLong(authentication.getPrincipal().toString());
            logger.info("Admin request to get all tickets, userId: {}", userId);

            List<Ticket> allTickets = ticketService.getAllTickets();

            List<TicketResponseDto> listOfAllTicketDto = allTickets.stream()
                    .map(ticket -> {
                        User ticketOwner = null;
                        if (ticket.getUserId() != null) {
                            ticketOwner = userService.getUserById(ticket.getUserId()).orElse(null);
                        }
                        return TicketDtoMapper.mapTicketToDto(ticket, ticketOwner);
                    })
                    .toList();

            logger.info("Returning {} tickets to admin userId: {}", listOfAllTicketDto.size(), userId);
            return ResponseEntity.ok(listOfAllTicketDto);
        } catch (Exception e) {
            logger.error("Failed to get all tickets", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}