package com.student.management.platform.security.user_management;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
class DataInitializer implements CommandLineRunner {

  @Value("${admin-pass}")
  private String adminPassword;
  private final ServiceUserRepository serviceUserRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;


  DataInitializer(ServiceUserRepository serviceUserRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
    this.serviceUserRepository = serviceUserRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    createAdminUser();
  }

  private void createAdminUser() {
    if (serviceUserRepository.findByUsername("admin").isEmpty()) {
      Role adminRole = roleRepository.findByName("admin").orElseGet(() -> {
        Role newRole = new Role();
        newRole.setName("admin");
        return newRole;
      });
      ServiceUser adminUser = new ServiceUser();
      adminUser.setUsername("admin");
      adminUser.setPassword(passwordEncoder.encode(adminPassword));
      Set<Role> roles = new HashSet<>();
      roles.add(adminRole);
      adminUser.setRoles(roles);
      roleRepository.save(adminRole);
      serviceUserRepository.save(adminUser);
    }
  }
}
