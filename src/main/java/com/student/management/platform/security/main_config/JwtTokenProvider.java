package com.student.management.platform.security.main_config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
class JwtTokenProvider {

  @Value("${jwt-secret-key}")
  private String secretKey;

  String createToken(String username, List<GrantedAuthority> authorities) {
    Claims claims = Jwts.claims().setSubject(username);
    claims.put("roles", authorities.stream().map(GrantedAuthority::getAuthority).toList());
    Date now = new Date();
    Date validity = new Date(now.getTime() + 3600000);
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
        .compact();
  }

  @SuppressWarnings("unchecked")
  Authentication getAuthentication(String token) {
    JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build();
    Claims claims = jwtParser.parseClaimsJws(token).getBody();
    List<GrantedAuthority> authorities = new ArrayList<>();
    List<String> roles = (List<String>) claims.get("roles");
    for (String authority : roles) {
      SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
      authorities.add(simpleGrantedAuthority);
    }
    return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);
  }

  synchronized boolean validateToken(String token, HttpServletResponse response) {
    try {
      JwtParser parser = Jwts.parserBuilder()
          .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
          .build();
      Jws<Claims> claims = parser.parseClaimsJws(token);
      return checkTokenValidityAndUpdateRefreshToken(claims, response);
    } catch (JwtException | IllegalArgumentException e) {
      throw new JwtAuthenticationException("Expired or invalid JWT token", e);
    }
  }

  @SuppressWarnings("unchecked")
  private boolean checkTokenValidityAndUpdateRefreshToken(Jws<Claims> claims, HttpServletResponse response) {
    Date expirationDate = claims.getBody().getExpiration();
    Date currentDate = new Date();
    long expirationMillis = expirationDate.getTime();
    long currentMillis = currentDate.getTime();
    long twentyMinutesInMillis = 20L * 60L * 1000L;
    long expirationTimeResult = expirationMillis - currentMillis;
    if (expirationTimeResult >= 0) {
      if (expirationMillis - currentMillis <= twentyMinutesInMillis) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        response.addHeader("Authorization",
            createToken(auth.getName(),
                (List<GrantedAuthority>) auth.getAuthorities()));
        response.addHeader("Refresh", "1");
      }
      return true;
    } else {
      return false;
    }
  }
}
