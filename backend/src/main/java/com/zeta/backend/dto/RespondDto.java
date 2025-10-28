package com.zeta.backend.dto;
import com.zeta.backend.enums.TicketPriority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespondDto {
    private String response;
    private TicketPriority Priority;
}

