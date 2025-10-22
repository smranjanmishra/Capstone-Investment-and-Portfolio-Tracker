package com.mycom.zeta.investment_portfolio_Tracker.utils;
import com.mycom.zeta.investment_portfolio_Tracker.dto.JwtResponseDto;
import com.mycom.zeta.investment_portfolio_Tracker.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtDtoMapper {
    private String token;
    private UserResponseDto userResponseDto;

    public static JwtResponseDto JwtMapperDto(String token, UserResponseDto userResponseDto){
        return new JwtResponseDto(token,userResponseDto);
    }
}
