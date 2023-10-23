package com.student.management.platform.security.user_management;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class CustomUserDetailService implements UserDetailsService {

  private final ServiceUserRepository serviceUserRepository;

  CustomUserDetailService(ServiceUserRepository serviceUserRepository) {
    this.serviceUserRepository = serviceUserRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    ServiceUser user = serviceUserRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    List<GrantedAuthority> authorities = new ArrayList<>();
    user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
    return new User(user.getUsername(), user.getPassword(), authorities);
  }
}
