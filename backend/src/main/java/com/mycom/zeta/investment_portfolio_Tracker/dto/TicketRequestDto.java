package com.mycom.zeta.investment_portfolio_Tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestDto {

    private String description;
    private Integer investmentId;
    private String subject;
}
