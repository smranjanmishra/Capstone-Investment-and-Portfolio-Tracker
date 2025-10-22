package com.mycom.zeta.investment_portfolio_Tracker.services;

import com.mycom.zeta.investment_portfolio_Tracker.entities.CustomUserDetails;
import com.mycom.zeta.investment_portfolio_Tracker.entities.User;
import com.mycom.zeta.investment_portfolio_Tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user=userRepository.findUserByEmail(email);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("User not found");

        }else{
            return new CustomUserDetails(user.get());
        }
    }


}
