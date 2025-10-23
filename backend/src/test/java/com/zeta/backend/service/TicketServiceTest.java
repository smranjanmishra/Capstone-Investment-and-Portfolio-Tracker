package com.zeta.backend.service;

import com.zeta.backend.enums.TicketStatus;
import com.zeta.backend.models.Ticket;
import com.zeta.backend.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceTest {

    private TicketRepository ticketRepository;
    private TicketService ticketService;

    @BeforeEach
    void setup() {
        ticketRepository = mock(TicketRepository.class);
        ticketService = new TicketService();
        org.springframework.test.util.ReflectionTestUtils.setField(ticketService, "ticketRepository", ticketRepository);
    }

    @Test
    void createTicket_setsFieldsAndSaves() {
        Ticket t = new Ticket();
        t.setSubject("This is urgent");
        t.setDescription("desc");
        t.setUserId(1);

        Ticket saved = new Ticket();
        saved.setId(1);
        saved.setSubject(t.getSubject());
        saved.setDescription(t.getDescription());
        saved.setUserId(1);
        saved.setCreatedAt(LocalDateTime.now());
        saved.setStatus(TicketStatus.OPEN);

        when(ticketRepository.save(any(Ticket.class))).thenReturn(saved);

        Ticket result = ticketService.createTicket(t);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void lifecycle_updateTicket_changesStatusAndResponse() {
        Ticket existing = new Ticket();
        existing.setId(10);
        existing.setStatus(TicketStatus.OPEN);
        existing.setResponse(null);

        when(ticketRepository.findById(10)).thenReturn(Optional.of(existing));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        Ticket updated = ticketService.updateTicket(10, TicketStatus.CLOSED, "Fixed");

        assertEquals(TicketStatus.CLOSED, updated.getStatus());
        assertEquals("Fixed", updated.getResponse());
        assertNotNull(updated.getUpdatedAt());
        verify(ticketRepository, times(1)).findById(10);
        verify(ticketRepository, times(1)).save(existing);
    }

    @Test
    void updateTicket_notFound_throws() {
        when(ticketRepository.findById(123)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> ticketService.updateTicket(123, TicketStatus.CLOSED, "x"));
        assertTrue(ex.getMessage().contains("Ticket not found"));
    }
}
