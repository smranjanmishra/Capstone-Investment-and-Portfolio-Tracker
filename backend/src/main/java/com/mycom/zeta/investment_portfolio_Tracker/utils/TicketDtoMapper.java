package com.mycom.zeta.investment_portfolio_Tracker.utils;
import com.mycom.zeta.investment_portfolio_Tracker.dto.TicketResponseDto;
import com.mycom.zeta.investment_portfolio_Tracker.dto.UserResponseDto;
import com.mycom.zeta.investment_portfolio_Tracker.entities.Ticket;
import com.mycom.zeta.investment_portfolio_Tracker.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
//It wont have autowired because it is responsible for carrying data from frontend to backend without exposing secret credential or without affecting any change in database it only work is to provide response to the server and request object from the https request

//They receive data from the request (for example, when a user submits a form)
//They send data back in the response (without exposing sensitive information like passwords)

public class TicketDtoMapper {
    private Ticket ticket;
    private User user;
    public static TicketResponseDto mapTicketToDto(Ticket ticket,User user){
        UserResponseDto userResponse=new UserResponseDto(user.getEmail(),user.getId(),user.getUserrole().toString());
        return new TicketResponseDto(ticket.getId(), ticket.getInvestmentId(), ticket.getTicketPriority(),ticket.getTicketStatus(),ticket.getSubject(),ticket.getDescription(),ticket.getResponse(),ticket.getCreatedAt(),ticket.getUpdatedAt(),userResponse);
    }
}
