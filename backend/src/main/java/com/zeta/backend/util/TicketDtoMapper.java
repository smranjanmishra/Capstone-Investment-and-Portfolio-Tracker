package com.zeta.backend.util;

import com.zeta.backend.dto.TicketResponseDto;
import com.zeta.backend.dto.UserResponse;
import com.zeta.backend.models.Ticket;
import com.zeta.backend.models.User;

public class TicketDtoMapper {

    // Maps a Ticket entity and its owner User to a TicketResponseDto
    public static TicketResponseDto mapTicketToDto(Ticket ticket, User user) {
        UserResponse userResponse = null;

        // If user is not null, map it to UserResponse
        if (user != null) {
            userResponse = UserResponse.fromUser(user);
        }

        return new TicketResponseDto(
                ticket.getId(),
                ticket.getInvestmentProductId(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getResponse(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                userResponse
        );
    }
}
