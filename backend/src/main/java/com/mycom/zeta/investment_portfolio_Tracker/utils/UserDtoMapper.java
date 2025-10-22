package com.mycom.zeta.investment_portfolio_Tracker.utils;

import com.mycom.zeta.investment_portfolio_Tracker.dto.UserResponseDto;
import com.mycom.zeta.investment_portfolio_Tracker.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoMapper {

    private User user;
    public static UserResponseDto UserMapperToDto(User user){
        return new UserResponseDto(user.getEmail(),user.getId(),user.getUserrole().toString());
    }

}
