/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import moneybuddy.fr.moneybuddy.model.enums.Role;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration}")
  private long jwtExpirationMs;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(UserDetails userDetails, String id, Role role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role.name());
    claims.put("id", id);

    return generateToken(claims, userDetails);
  }

  public String generateSubAccountToken(
      String subAccountId, String id, String email, SubAccountRole subAccountRole, Role role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("subAccountId", subAccountId);
    claims.put("email", email);
    claims.put("subAccountRole", subAccountRole.name());
    claims.put("role", role.name());
    claims.put("id", id);

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractSubAccountId(String token) {
    return extractClaim(token, claims -> claims.get("subAccountId", String.class));
  }

  public String extractSubAccountEmail(String token) {
    return extractClaim(token, claims -> claims.get("email", String.class));
  }

  public String extractSubAccountAccountId(String token) {
    return extractClaim(token, claims -> claims.get("id", String.class));
  }

  public SubAccountRole extractSubAccountRole(String token) {
    String roleString = extractClaim(token, claims -> claims.get("subAccountRole", String.class));
    SubAccountRole role = SubAccountRole.valueOf(roleString);
    return role;
  }

  public Role extractAccountRole(String token) {
    String roleString = extractClaim(token, claims -> claims.get("role", String.class));
    Role role = Role.valueOf(roleString);
    return role;
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractAccountId(String token) {
    return extractClaim(token, claims -> claims.get("id", String.class));
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
