package com.mycom.zeta.investment_portfolio_Tracker.repository;

import com.mycom.zeta.investment_portfolio_Tracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);
    Optional<User>findUserById(Integer Id);
}
