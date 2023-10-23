package com.student.management.platform.security.user_management;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
interface ServiceUserRepository extends JpaRepository<ServiceUser, Integer> {
  @EntityGraph(attributePaths = "roles", type = EntityGraph.EntityGraphType.LOAD)
  Optional<ServiceUser> findByUsername(String username);

  @Transactional
  @Modifying
  void deleteByUsername(String username);
}
