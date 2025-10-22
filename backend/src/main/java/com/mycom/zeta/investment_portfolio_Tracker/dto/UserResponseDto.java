package com.mycom.zeta.investment_portfolio_Tracker.dto;

import com.mycom.zeta.investment_portfolio_Tracker.enums.Userrole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String email;
    private Integer id;
    private String userrole;
}
