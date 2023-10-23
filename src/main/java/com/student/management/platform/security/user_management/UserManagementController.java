package com.student.management.platform.security.user_management;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/management")
class UserManagementController {

  private final UserManagementService managementService;

  UserManagementController(UserManagementService managementService) {
    this.managementService = managementService;
  }

  @PostMapping
  public String createNewUser(HttpServletResponse response, @RequestBody UserDTO user) {
    try {
      managementService.createUser(user);
      return "successfully created user: " + user.name();
    } catch (Exception e) {
      response.setStatus(500);
      return e.getMessage();
    }
  }

  @PutMapping
  public String updateUser(HttpServletResponse response, @RequestBody UserDTO user) {
    try {
      managementService.updateUser(user);
      return "successfully updated user: " + user.name();
    } catch (Exception e) {
      response.setStatus(500);
      return e.getMessage();
    }
  }

  @DeleteMapping
  public String deleteUser(HttpServletResponse response, @RequestParam String username) {
    try {
      managementService.deleteUser(username);
      return "successfully deleted user: " + username;
    } catch (Exception e) {
      response.setStatus(500);
      return e.getMessage();
    }
  }

  @PostMapping("/features")
  public String createRole(HttpServletResponse response, @RequestParam("role") String role) {
    try {
      managementService.createRole(role);
      return "successfully created role: " + role;
    } catch (Exception e) {
      response.setStatus(500);
      return e.getMessage();
    }
  }

  @DeleteMapping("/features")
  public String deleteRole(HttpServletResponse response, @RequestParam("role") String role) {
    try {
      managementService.deleteRole(role);
      return "successfully deleted role: " + role;
    } catch (Exception e) {
      response.setStatus(500);
      return e.getMessage();
    }
  }

}
