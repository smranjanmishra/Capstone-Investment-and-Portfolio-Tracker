package com.mycom.zeta.investment_portfolio_Tracker.dto;

import com.mycom.zeta.investment_portfolio_Tracker.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponseDto {
    private String token;
    private UserResponseDto userResponse;
}
