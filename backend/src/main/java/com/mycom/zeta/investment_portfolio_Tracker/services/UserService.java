package com.mycom.zeta.investment_portfolio_Tracker.services;

import com.mycom.zeta.investment_portfolio_Tracker.entities.User;
import com.mycom.zeta.investment_portfolio_Tracker.enums.Userrole;
import com.mycom.zeta.investment_portfolio_Tracker.repository.UserRepository;
import com.mycom.zeta.investment_portfolio_Tracker.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil){
        this.jwtUtil=jwtUtil;
        this.userRepository=userRepository;

    }



    public Optional<User> getUserById(Integer userId) {
        return userRepository.findUserById(userId);
    }


    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public boolean isAdmin(User user) {
        return user.getUserrole()== Userrole.ADMIN;
    }



    // to get the logged in user
    // if we are using jwt then getUserByJwt will be invoked
    // if we want to use the natural SPRING SECURITY then we simply get the logged in user from jwt
    public User getLoggedInUser(HttpServletRequest httpServletRequest, boolean useJwt) {
       return getUserByJwt(httpServletRequest);
    }


    //helper  method to get user from jwt

    public User getUserByJwt(HttpServletRequest httpServletRequest){
        String header=httpServletRequest.getHeader("Authorization");

        if(header==null || !header.startsWith("Bearer "))throw  new RuntimeException("Invalid authorization header");

        String token=header.substring(7);
        String email=jwtUtil.extractUsermail(token);
        Optional<User>userOptional=getUserByEmail(email);
        if(userOptional.isEmpty())throw  new RuntimeException("User Not Found");
        return userOptional.get();
    }


    // Register new User

    public User registerUser(User user){
        //checking if user already exist

        String email =user.getEmail();
        if(userRepository.findUserByEmail(email).isPresent()){
            throw new RuntimeException("User already exists");
        }

        // registering the user

        User register=new User();
        register.setEmail(user.getEmail());
        register.setPassword(passwordEncoder.encode(user.getPassword()));
        register.setUserrole(user.getUserrole()==null?Userrole.USERS:user.getUserrole());
        userRepository.save(register);
        return register;
}

}
