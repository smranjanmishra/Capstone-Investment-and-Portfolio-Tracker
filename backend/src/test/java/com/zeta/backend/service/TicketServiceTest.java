package com.zeta.backend.service;

import com.zeta.backend.enums.Role;
import com.zeta.backend.enums.TicketPriority;
import com.zeta.backend.enums.TicketStatus;
import com.zeta.backend.models.Ticket;
import com.zeta.backend.models.User;
import com.zeta.backend.repository.TicketRepository;
import com.zeta.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user.setPasswordHash("password");
        user.setRole(Role.USER);// Set required fields for User entity
        testUser = userRepository.save(user); // Save the user and get the managed entity
        assertNotNull(testUser.getId(), "Test user should have an ID after saving");
    }

    @Test
    void createTicket_setsFieldsAndSaves() {
        Ticket newTicket = new Ticket();
        newTicket.setSubject("This is urgent problem"); // Contains keywords for priority
        newTicket.setDescription("Need help immediately");
        newTicket.setUserId(testUser.getId()); // Associate with the created user
        Ticket savedTicket = ticketService.createTicket(newTicket);

        assertNotNull(savedTicket, "Saved ticket should not be null");
        assertNotNull(savedTicket.getId(), "Saved ticket should have an ID");
        assertEquals(testUser.getId(), savedTicket.getUserId());
        assertEquals("This is urgent problem", savedTicket.getSubject());
        assertEquals(TicketStatus.OPEN, savedTicket.getStatus(), "Status should be OPEN initially");
        assertEquals(TicketPriority.HIGH, savedTicket.getPriority(), "Priority should be HIGH based on subject");
        assertNotNull(savedTicket.getCreatedAt(), "CreatedAt timestamp should be set");
        assertNull(savedTicket.getResponse(), "Response should be null initially");
        assertNull(savedTicket.getUpdatedAt(), "UpdatedAt should be null initially");

        // Verify it exists in the database
        Optional<Ticket> foundTicket = ticketRepository.findById(savedTicket.getId());
        assertTrue(foundTicket.isPresent(), "Ticket should be found in repository");
        assertEquals(TicketStatus.OPEN, foundTicket.get().getStatus());
    }

    @Test
    void lifecycle_updateTicket_changesStatusAndResponse() {
        Ticket initialTicket = new Ticket();
        initialTicket.setSubject("Initial Issue");
        initialTicket.setDescription("Please investigate");
        initialTicket.setUserId(testUser.getId());
        initialTicket.setStatus(TicketStatus.OPEN);
        initialTicket.setPriority(TicketPriority.LOW);
        initialTicket.setCreatedAt(LocalDateTime.now().minusDays(1)); // Set a creation time
        Ticket savedInitialTicket = ticketRepository.save(initialTicket);
        Integer ticketId = savedInitialTicket.getId();
        assertNotNull(ticketId, "Initial ticket should have an ID");

        // Act: Update the ticket using the service
        String adminResponse = "This issue has been resolved.";
        Ticket updatedTicket = ticketService.updateTicket(ticketId, TicketStatus.RESPONDED, adminResponse);

        assertNotNull(updatedTicket, "Updated ticket should not be null");
        assertEquals(ticketId, updatedTicket.getId());
        assertEquals(TicketStatus.RESPONDED, updatedTicket.getStatus(), "Status should be updated to RESPONDED");
        assertEquals(adminResponse, updatedTicket.getResponse(), "Response should be updated");
        assertNotNull(updatedTicket.getUpdatedAt(), "UpdatedAt timestamp should now be set");
        assertEquals(savedInitialTicket.getCreatedAt(), updatedTicket.getCreatedAt(), "CreatedAt should not change");

        Optional<Ticket> foundAfterUpdate = ticketRepository.findById(ticketId);
        assertTrue(foundAfterUpdate.isPresent(), "Ticket should still exist after update");
        assertEquals(TicketStatus.RESPONDED, foundAfterUpdate.get().getStatus());
        assertEquals(adminResponse, foundAfterUpdate.get().getResponse());
        assertNotNull(foundAfterUpdate.get().getUpdatedAt());
    }



    @Test
    void updateTicket_notFound_throwsRuntimeException() {
        Integer nonExistentTicketId = -999;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            ticketService.updateTicket(nonExistentTicketId, TicketStatus.CLOSED, "This won't work");
        });
        assertTrue(ex.getMessage().toLowerCase().contains("ticket not found"),
                "Exception message should indicate ticket not found");

    }

    @Test
    void getTicketById_found() {
        Ticket ticket = new Ticket();
        ticket.setSubject("Find Me");
        ticket.setUserId(testUser.getId());
        Ticket saved = ticketRepository.save(ticket);
        Optional<Ticket> found = ticketService.getTicketById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void getTicketById_notFound() {
        Optional<Ticket> found = ticketService.getTicketById(-1);
        assertFalse(found.isPresent());
    }

}

