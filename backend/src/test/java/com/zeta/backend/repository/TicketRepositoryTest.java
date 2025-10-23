package com.zeta.backend.repository;

import com.zeta.backend.models.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void saveAndFindByUserId_andFindById() {
        Ticket t = new Ticket();
        t.setSubject("repo test");
        t.setDescription("desc");
        t.setUserId(5);
        ticketRepository.save(t);

        List<Ticket> byUser = ticketRepository.findByUserId(5);
        assertTrue(!byUser.isEmpty());

        Optional<Ticket> byId = ticketRepository.findById(byUser.get(0).getId());
        assertTrue(byId.isPresent());
    }
}
