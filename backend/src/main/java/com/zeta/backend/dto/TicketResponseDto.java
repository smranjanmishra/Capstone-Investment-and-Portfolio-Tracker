package com.zeta.backend.dto;

import com.zeta.backend.enums.TicketPriority;
import com.zeta.backend.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDto {
    private Integer id;
    private Long investmentProductId;
    private TicketPriority ticketPriority;
    private TicketStatus ticketStatus;
    private String subject;
    private String description;
    private String response;
    private LocalDateTime createdAt=LocalDateTime.now();
    private LocalDateTime updatedAt;
    private UserResponse userResponse;
}
