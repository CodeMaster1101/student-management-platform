package com.student.management.platform.students;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
interface StudentRepository extends JpaRepository<Student, Integer> {

  @Query("SELECT new com.student.management.platform.students.StudentDto(s) FROM Student s")
  Set<StudentDto> fetchStudentsDTO();

  @Query("SELECT s.id FROM Student s WHERE s.name = ?1")
  Integer findIdByName(String name);

  @Transactional
  @Modifying
  void deleteByName(String name);

  boolean existsByName(String name);
}
