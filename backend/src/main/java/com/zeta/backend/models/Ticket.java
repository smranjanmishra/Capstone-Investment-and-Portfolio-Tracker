package com.zeta.backend.models;

import com.zeta.backend.enums.TicketPriority;
import com.zeta.backend.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "supportTicket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "userId", nullable = false)
    private Long userId;  // FK to User

    @Column(name = "investmentProductId")
    private Long investmentProductId;  // FK to Investment

    @Column(columnDefinition = "VARCHAR(255)")
    private String subject;  // subject of the issue

    @Column(columnDefinition = "TEXT")
    private String description;  // Detailed description of the issue

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TicketPriority priority = TicketPriority.MEDIUM;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

}