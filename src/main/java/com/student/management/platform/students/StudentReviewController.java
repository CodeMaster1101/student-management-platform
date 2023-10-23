package com.student.management.platform.students;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/students")
class StudentReviewController {

  private final StudentsService studentsService;

  StudentReviewController(StudentsService studentsService) {
    this.studentsService = studentsService;
  }

  @GetMapping
  public Set<StudentDto> getStudents() {
    return studentsService.getAllStudents();
  }

  @PostMapping
  @SneakyThrows
  public ResponseEntity<String> injectNewSuccessfulStudent(
      @RequestParam("image") MultipartFile image,
      @RequestParam("studentDTO") String dto) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      StudentDto studentDTO = objectMapper.readValue(dto, StudentDto.class);
      studentsService.injectNewStudent(studentDTO, image);
      return new ResponseEntity<>("Student created/updated successfully", HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Unable to create new student", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

//  @PostMapping
//  public void registerNewStudent() {
//
//  }

  @DeleteMapping("/{name}")
  public ResponseEntity<String> deleteStudent(@PathVariable("name") String name) {
    try {
      studentsService.deleteStudentByName(name);
      return new ResponseEntity<>("Student deleted successfully", HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Unable to delete student", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
