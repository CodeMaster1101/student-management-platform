package com.student.management.platform.security.user_management;

import com.student.management.platform.global.MasterException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class UserManagementService  {

  @Value("${admin-pass}")
  private String adminPassword;
  private final ServiceUserRepository serviceUserRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;


  UserManagementService(ServiceUserRepository serviceUserRepository,
      RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
    this.serviceUserRepository = serviceUserRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }


  void createRole(String roleName) {
    if (!roleRepository.findByName(roleName).isPresent()) {
      Role role = new Role();
      role.setName(roleName);
      roleRepository.save(role);
    } else {
      throw new MasterException("Role exists with name: " + roleName, new RuntimeException());
    }
  }
  void createUser(UserDTO userDTO) {
    String username = userDTO.name();
    if (!serviceUserRepository.findByUsername(username).isPresent()) {
      ServiceUser user = new ServiceUser();
      user.setUsername(username);
      user.setPassword(passwordEncoder.encode(userDTO.password()));
      user.setRoles(roleRepository.findAllIn(userDTO.features()));
      serviceUserRepository.save(user);
    } else {
      throw new MasterException("User exists with username: " + username, new RuntimeException());
    }
  }

  void updateUser(UserDTO userDTO) {
    Optional<ServiceUser> optionalUser = serviceUserRepository.findByUsername(userDTO.name());
    if (optionalUser.isPresent()) {
      ServiceUser user = optionalUser.get();
      user.setPassword(passwordEncoder.encode(userDTO.password()));
      user.setRoles(roleRepository.findAllIn(userDTO.features()));
      serviceUserRepository.save(user);
    }
  }

  void deleteUser(String username) {
    serviceUserRepository.deleteByUsername(username);
  }

  void deleteRole(String role) {
    roleRepository.deleteByName(role);
  }
}
