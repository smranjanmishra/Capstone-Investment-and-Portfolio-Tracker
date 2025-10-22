package com.mycom.zeta.investment_portfolio_Tracker.dto;

import com.mycom.zeta.investment_portfolio_Tracker.enums.TicketPriority;
import com.mycom.zeta.investment_portfolio_Tracker.enums.TicketStatus;
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
    private Integer investmentId;
    private TicketPriority ticketPriority;
    private TicketStatus ticketStatus;
    private String subject;
    private String description;
    private String response;
    private LocalDateTime createdAt=LocalDateTime.now();
    private LocalDateTime updatedAt;
    private UserResponseDto userResponse;
}
