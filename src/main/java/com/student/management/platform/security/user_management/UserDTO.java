package com.student.management.platform.security.user_management;

import java.util.Set;

record UserDTO(String name, String password, Set<String> features) {}
