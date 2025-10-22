package com.zeta.backend.models;

import com.zeta.backend.enums.TicketPriority;
import com.zeta.backend.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@Entity
@Table(name="support_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId; // Foreign key to user

    @Column(columnDefinition ="TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String subject;

    private Integer investmentId;    // Foreign key to investment ;

    @Enumerated(EnumType.STRING)
    private TicketPriority ticketPriority=TicketPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus=TicketStatus.OPEN;

    @Column(columnDefinition = "TEXT")
    private String response;
    private LocalDateTime createdAt= LocalDateTime.now();
    private LocalDateTime updatedAt;
}
