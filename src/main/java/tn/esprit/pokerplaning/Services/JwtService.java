package tn.esprit.pokerplaning.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.pokerplaning.Entities.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service

public class JwtService {

    private  final String SECRET_KEY = "4d5cb175447d7357c1eff44a9943bbef945d7bc56a88e6d858a8975fad88fd7ae77b3d7ebe9be76c479a29a39ad66720ecf505726abd0c5b6c99c848c0789372b99e2c6f987bacc86433c10e6b187456dbf2449fe4f75ec6dc3ace24444fdcd58659b21e4136646871d5ccd1b919653ef8114dd6a22b6880df19b136508d8fb5\n";


    public Boolean isTokenValid(String token , UserDetails userDetails) {

        final String username = extractUsername(token);
        return ( username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return  extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim( String token , Function<Claims, T> Resolver) {
        final Claims claims = extractAllClaims(token);
        return Resolver.apply(claims);
    }

    public String extractUsername(String token) {
        return  extractClaim(token, Claims::getSubject); //subject can be username or email
    }
    private Claims extractAllClaims( String token)
    {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSingInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(User user)
    {
         String token = Jwts.builder()
                 .setSubject(user.getUsername())
                 .setIssuedAt(new Date(System.currentTimeMillis()))
                 .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 ))
                 .signWith(getSingInKey(), SignatureAlgorithm.HS256)
                 .compact();

         return token;
    }

    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
