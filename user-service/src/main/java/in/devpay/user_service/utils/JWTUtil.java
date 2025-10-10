package in.devpay.user_service.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {
    private static final String SECRET = "gdxsfgqaswfqywfuyhjcgxfwyasrdytqwredug";

    private Key getSignedKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String extractEmail(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignedKey())
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String userName){
        try {
            extractEmail(token);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignedKey())
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .getSubject();
    }

    public String generateToken(Map<String, Object> claims, String email){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSignedKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractRole(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignedKey())
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .get("role")
                .toString();
    }
}
