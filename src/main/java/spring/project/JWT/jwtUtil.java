package spring.project.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class jwtUtil {
    private String secret = "btechdays";


    public String extractUsername(String token){
        return extractClamis(token , Claims::getSubject);
    }
//   getSubject is one of the method in the Claims interface, you can open it once and see
    public Date extractExpiration(String token){
        return extractClamis(token , Claims::getExpiration);
    }
//   getExpiration is one of the method in the Claims interface
    private <T> T extractClamis(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
//    extractClaims is a function for which parameters are token and a method that decides
//    which part to extract.
//    its return value it T that is it can be anything depends on the method parameter i.e
//    String, Integer etc..
//    claimsresolver can be any function like extractusername, extractexpiration which takes
//    Claims as input and T as output
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }



    public String generateToken(String Username , String role){
        Map<String , Object> claims = new HashMap<>();
        claims.put("role" , role);
        return createtoken(claims, Username);
    }
//    Claims are nothing but the details present inside the payload part of the token which
//    is a map in which keys are strings
//    In Java, Object is the root class of the Java class hierarchy. Every class in Java is a
//    subclass (directly or indirectly) of the Object class. This means that a variable of type
//    Object can hold a reference to any Java object, regardless of its class.
//    you can put String, Integer, Boolean, List, or even custom objects as values in the map.
    private String createtoken(Map<String , Object> claims , String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*10))
                .signWith(SignatureAlgorithm.HS256,secret).compact();
    }
//    each time it modifies and returns the same JwtBuilder object through successive setter calls




    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public Boolean validatetoken(String token , UserDetails userDetails){
        final String Username = extractUsername(token);
        return (Username.equals(userDetails.getUsername()) && !isTokenExpired(token) );
    }
//    here in validation, 2 things are verified.
//    a)username from the token is extracted and using that username, its corresponding details
//      from the database is acquired and then compared.
//    b)checks the expiry time of token
}