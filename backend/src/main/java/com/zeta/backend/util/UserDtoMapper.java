package com.zeta.backend.util;

import com.zeta.backend.dto.UserResponse;
import com.zeta.backend.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoMapper {
    private User user;
    public static UserResponse UserMapperToDto(User user){
        return new UserResponse(user.getEmail(), Math.toIntExact(user.getId()),user.getRole().toString());
    }

}
