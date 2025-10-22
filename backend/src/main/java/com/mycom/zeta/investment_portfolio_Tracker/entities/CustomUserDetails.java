package com.mycom.zeta.investment_portfolio_Tracker.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private User user;

  public CustomUserDetails(User user){
      this.user=user;

  }

  public User getUser(){
      return user;

  }
  @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){

      // It maps the authorities of the user to Spring security authority
      return Collections.singletonList(new SimpleGrantedAuthority(user.getUserrole().name()));
  }

  @Override
    public String getPassword(){
      return user.getPassword();
  }
    @Override
    public String getUsername() {
        return user.getEmail(); // use email as username
    }

}
