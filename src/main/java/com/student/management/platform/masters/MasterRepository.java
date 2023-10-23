 package com.student.management.platform.masters;

 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.data.jpa.repository.Modifying;
 import org.springframework.data.jpa.repository.Query;
 import org.springframework.stereotype.Repository;
 import org.springframework.transaction.annotation.Transactional;

 import java.util.Set;

@Repository
@Transactional(readOnly = true)
interface MasterRepository extends JpaRepository<Master, Integer> {

  @Query("SELECT new com.student.management.platform.masters.MasterDto(m)" +
      " FROM Master m")
  Set<MasterDto> findAllSet();

  @Query("SELECT m.id FROM Master m WHERE m.name = ?1")
  Integer findIdByName(String name);

  @Transactional
  @Modifying
  void deleteByName(String name);

  boolean existsByName(String name);
}
