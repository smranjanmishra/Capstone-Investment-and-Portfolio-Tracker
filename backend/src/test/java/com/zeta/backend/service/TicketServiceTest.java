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
        user.setPasswordHash("password"); // Corrected field name based on previous errors
        user.setRole(Role.USER); // Set a default role
        testUser = userRepository.save(user);
        assertNotNull(testUser.getId(), "Test user should have an ID after saving");
    }

    @Test
    void createTicket_setsFieldsAndSaves() {

        Ticket newTicket = new Ticket();
        newTicket.setSubject("This is urgent problem");
        newTicket.setDescription("Need help immediately");
        newTicket.setUserId(testUser.getId());
        Ticket savedTicket = ticketService.createTicket(newTicket);
        assertNotNull(savedTicket);
        assertNotNull(savedTicket.getId());
        assertEquals(testUser.getId(), savedTicket.getUserId());
        assertEquals("This is urgent problem", savedTicket.getSubject());
        assertEquals(TicketStatus.OPEN, savedTicket.getStatus());
        assertEquals(TicketPriority.HIGH, savedTicket.getPriority());
        assertNotNull(savedTicket.getCreatedAt());
        assertNull(savedTicket.getResponse());
        assertNull(savedTicket.getUpdatedAt());
        Optional<Ticket> foundTicket = ticketRepository.findById(savedTicket.getId());
        assertTrue(foundTicket.isPresent());
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
        initialTicket.setCreatedAt(LocalDateTime.now().minusDays(1));
        Ticket savedInitialTicket = ticketRepository.save(initialTicket);
        Integer ticketId = savedInitialTicket.getId();
        assertNotNull(ticketId);
        String adminResponse = "This issue has been resolved.";
        Ticket updatedTicket = ticketService.updateTicket(ticketId, TicketStatus.RESPONDED, adminResponse, null);
        assertNotNull(updatedTicket);
        assertEquals(ticketId, updatedTicket.getId());
        assertEquals(TicketStatus.RESPONDED, updatedTicket.getStatus());
        assertEquals(adminResponse, updatedTicket.getResponse());
        assertEquals(TicketPriority.LOW, updatedTicket.getPriority());
        assertNotNull(updatedTicket.getUpdatedAt());
        assertEquals(savedInitialTicket.getCreatedAt(), updatedTicket.getCreatedAt());
        Optional<Ticket> foundAfterUpdate = ticketRepository.findById(ticketId);
        assertTrue(foundAfterUpdate.isPresent());
        assertEquals(TicketStatus.RESPONDED, foundAfterUpdate.get().getStatus());
        assertEquals(adminResponse, foundAfterUpdate.get().getResponse());
        assertEquals(TicketPriority.LOW, foundAfterUpdate.get().getPriority());
        assertNotNull(foundAfterUpdate.get().getUpdatedAt());
    }
    @Test
    void updateTicket_setsNewPriorityWhenProvided() {
        Ticket initialTicket = new Ticket();
        initialTicket.setSubject("Priority Test");
        initialTicket.setUserId(testUser.getId());
        initialTicket.setStatus(TicketStatus.OPEN);
        initialTicket.setPriority(TicketPriority.LOW); // Start with LOW
        initialTicket.setCreatedAt(LocalDateTime.now().minusDays(1));
        Ticket savedInitialTicket = ticketRepository.save(initialTicket);
        Integer ticketId = savedInitialTicket.getId();
        assertNotNull(ticketId);
        String adminResponse = "Setting priority high.";
        Ticket updatedTicket = ticketService.updateTicket(ticketId, TicketStatus.RESPONDED, adminResponse, TicketPriority.HIGH);
        assertNotNull(updatedTicket);
        assertEquals(ticketId, updatedTicket.getId());
        assertEquals(TicketStatus.RESPONDED, updatedTicket.getStatus());
        assertEquals(adminResponse, updatedTicket.getResponse());
        assertEquals(TicketPriority.HIGH, updatedTicket.getPriority()); // Assert priority was updated
        assertNotNull(updatedTicket.getUpdatedAt());
        Optional<Ticket> foundAfterUpdate = ticketRepository.findById(ticketId);
        assertTrue(foundAfterUpdate.isPresent());
        assertEquals(TicketPriority.HIGH, foundAfterUpdate.get().getPriority());
    }

    @Test
    void updateTicket_notFound_throwsRuntimeException() {
        Integer nonExistentTicketId = -999;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            ticketService.updateTicket(nonExistentTicketId, TicketStatus.CLOSED, "This won't work", null);
        });
        assertTrue(ex.getMessage().toLowerCase().contains("ticket not found"),
                "Exception message should indicate ticket not found");
    }

    @Test
    void getTicketById_found() {
        Ticket ticket = new Ticket();
        ticket.setSubject("Find Me");
        ticket.setUserId(testUser.getId());
        ticket.setPriority(TicketPriority.LOW);
        ticket.setStatus(TicketStatus.OPEN);
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

