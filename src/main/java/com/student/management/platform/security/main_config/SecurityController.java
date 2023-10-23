package com.student.management.platform.security.main_config;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/security")
class SecurityController {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final JwtTokenProvider jwtTokenProvider;

  SecurityController(AuthenticationManagerBuilder authenticationManagerBuilder, JwtTokenProvider jwtTokenProvider) {
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @PostMapping("/login")
  @SuppressWarnings("unchecked")
  public ResponseEntity<String> processLogin(@RequestBody LoginForm loginForm) {
    try {
      String username = loginForm.username();
      String password = loginForm.password();
      Authentication authentication = authenticationManagerBuilder.getOrBuild().authenticate(new UsernamePasswordAuthenticationToken(username, password));
      String token = jwtTokenProvider.createToken(username, (List<GrantedAuthority>) authentication.getAuthorities());
      return ResponseEntity.ok().header("Authorization",token).body("Login successful");
    } catch (AuthenticationException e) {
      return ResponseEntity.badRequest().body("Invalid username or password");
    }
  }

  @PostMapping("/validate-auth")
  public void validateAuthToken(@RequestParam("token") String token, HttpServletResponse response) {
    boolean validateToken = jwtTokenProvider.validateToken(token, response);
    if (validateToken) {
      response.setStatus(200);
    } else {
      response.setStatus(401);
    }
  }

  @PostMapping("/logout")
  public void logout(HttpServletResponse response) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      SecurityContextHolder.getContext().setAuthentication(null);
    }
    response.setStatus(200);
  }

}

