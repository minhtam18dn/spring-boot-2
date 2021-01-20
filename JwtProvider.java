package com.dsoft.m2u.security;

import java.util.Date;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.dsoft.m2u.domain.Token;
import com.dsoft.m2u.service.UserPrinciple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.DefaultClock;
/**
 * [Description]:<br>
 * [ Remarks ]:<br>
 * [Copyright]: Copyright (c) 2020<br>
 * 
 * @author D-Soft Joint Stock Company
 * @version 1.0
 */
@Component
public class JwtProvider {

	private static final Logger logger = LogManager.getLogger(JwtProvider.class);
	
    private Clock clock = DefaultClock.INSTANCE;

    @Value("dSoftSecretKey")
    private String jwtSecret;

    @Value("1800")
    private Long jwtExpiration;

    public Token generateJwtToken(Authentication authentication) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();

        Token token = new Token();
        token.setTokenId(Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact());
        token.setExpiredDate(expirationDate);
        return token;
    }

    public String refreshToken(String token) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public UserPrinciple getUserFromLogin(Authentication authentication) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        return userPrinciple;
    }

    public String getUserFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).getBody();
            return true;
        } catch (SignatureException e) {
            logger.warn("Invalid JWT signature -> Message: " + e.getMessage());
        } catch (MalformedJwtException e) {
            logger.warn("Invalid JWT token -> Message: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.warn("Expired JWT token -> Message: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("Unsupported JWT token -> Message: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string is empty -> Message: " + e.getMessage());
        }
        return false;
    }

    public Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + jwtExpiration * 1000);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(clock.now());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Boolean ignoreTokenExpiration(String token) {
        return false;
    }

    public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

}
