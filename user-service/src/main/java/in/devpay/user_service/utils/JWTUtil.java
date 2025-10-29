package in.devpay.user_service.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JWTUtil {

    @Value("${spring.application.secret}")
    private String secret;

    @Value("${spring.application.expiration}")
    private Long expiration;

    public String getJwtFromHeader(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");

        log.debug("Authorization Header: {}", authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        log.error("Authentication failed");
        return  null;
    }

    public String generateTokenFromUsername(DevpayUserDetails userDetails) {
        String username = userDetails.getUsername();

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignedKey())
                .compact();
    }

    public String getUsernameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) getSignedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private Key getSignedKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public boolean validateToken(String authToken){
        try {
            log.debug("Validate: {}", authToken);
            Jwts.parser().verifyWith((SecretKey) getSignedKey()).build().parseSignedClaims(authToken);
            return true;
        }
        catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        }
        catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        }
        catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
