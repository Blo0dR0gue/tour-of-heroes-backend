package com.example.demo.auth.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.demo.AppConstants;
import com.example.demo.auth.services.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {

    @Value("${demo.app.jwtSecret}")
    private String jwtSecret;

    @Value("${demo.app.jwtExpiration}")
    private int jwtExpiration;

    @Value("${demo.app.twoFATokenValid}")
    private int twoFATokenValid = 300;


    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        return false;
    }

    public String generateToken(UserDetailsImpl userDetails, boolean authenticated) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(AppConstants.AUTHENTICATED, authenticated);    //AUTHENTICATED as claim
        return doGenerateToken(claims, userDetails.getUsername(), authenticated);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, Boolean using2FA) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate, using2FA);

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(createdDate)
                .setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public String refreshToken(String token) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate, false);    //TODO using2FA

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public Boolean validateToken(String token, UserDetailsImpl userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Date calculateExpirationDate(Date createdDate, Boolean using2FA) {
        int expirationTime = using2FA ? twoFATokenValid * 1000 : jwtExpiration * 1000;
        return new Date(createdDate.getTime() + expirationTime);
    }

    public Boolean isAuthenticated(String token) {
        return this.getAllClaimsFromToken(token).get(AppConstants.AUTHENTICATED, Boolean.class);
    }

}