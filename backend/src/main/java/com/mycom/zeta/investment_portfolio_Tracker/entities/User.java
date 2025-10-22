package com.mycom.zeta.investment_portfolio_Tracker.entities;
import com.mycom.zeta.investment_portfolio_Tracker.enums.Userrole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@Entity
@Table (name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    @Column(updatable = false,nullable = false)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private Userrole userrole=Userrole.USERS;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private LocalDateTime createdAt=LocalDateTime.now();
}
