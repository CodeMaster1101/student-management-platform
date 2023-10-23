package com.student.management.platform.security.user_management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
interface RoleRepository extends JpaRepository<Role, Integer> {

  Optional<Role> findByName(String role);

  @Query("SELECT r FROM Role r WHERE r.name IN ?1")
  Set<Role> findAllIn(Set<String> features);

  @Transactional
  @Modifying
  void deleteByName(String role);
}
