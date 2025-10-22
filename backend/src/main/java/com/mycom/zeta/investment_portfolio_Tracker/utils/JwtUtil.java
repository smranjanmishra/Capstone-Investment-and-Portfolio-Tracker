package com.mycom.zeta.investment_portfolio_Tracker.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "fa1862e0fcea72ffe2fe57e3df1b4e135b8be2ef8155291e8ee60602e2ea0e13";

    public String extractUsermail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    // this is the function for extracting certain part of the claims object

    //for  in the extract username it will extract all claims from the jwt and then the resolver function will check for getsubject in the field of claim we have sub it will retrive that

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder().setClaims(claims).setSubject(email).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)).signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();

    }

    // method to check the token generated is valid or not
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsermail(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
