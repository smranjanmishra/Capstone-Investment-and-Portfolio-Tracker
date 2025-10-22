package com.mycom.zeta.investment_portfolio_Tracker.controller;

import com.mycom.zeta.investment_portfolio_Tracker.dto.JwtResponseDto;
import com.mycom.zeta.investment_portfolio_Tracker.dto.UserResponseDto;
import com.mycom.zeta.investment_portfolio_Tracker.entities.User;
import com.mycom.zeta.investment_portfolio_Tracker.services.CustomUserDetailsService;
import com.mycom.zeta.investment_portfolio_Tracker.services.UserService;
import com.mycom.zeta.investment_portfolio_Tracker.utils.JwtDtoMapper;
import com.mycom.zeta.investment_portfolio_Tracker.utils.JwtUtil;
import com.mycom.zeta.investment_portfolio_Tracker.utils.UserDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<?>registerUser(@RequestBody User user){
        try{
            User registeredUser=userService.registerUser(user);
            UserResponseDto userResponseDto= UserDtoMapper.UserMapperToDto(user);
            return ResponseEntity.ok("User registered successfully with email: " + userResponseDto.getEmail());
        } catch (Exception e) {
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Login user and generate Jwt token

    @PostMapping("/login")
    public ResponseEntity<?>loginUser(@RequestBody User user){
        try{
            Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword()));
            UserDetails userDetails= customUserDetailsService.loadUserByUsername(user.getEmail());
            Optional<User> loggedInUserOptional=userService.getUserByEmail(user.getEmail());
            if(loggedInUserOptional.isEmpty()){
                return ResponseEntity.badRequest().body("User not found ");
            }
            User loggedInUser=loggedInUserOptional.get();
            UserResponseDto userResponseDto=UserDtoMapper.UserMapperToDto(loggedInUser);
            String jwtToken= jwtUtil.generateToken(userDetails.getUsername());
            JwtResponseDto jwtResponse= JwtDtoMapper.JwtMapperDto(jwtToken,userResponseDto);
            return ResponseEntity.ok("JWT Token : "+jwtResponse.getToken());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

    }


}
