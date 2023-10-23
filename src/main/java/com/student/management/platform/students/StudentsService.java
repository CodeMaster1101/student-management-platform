package com.student.management.platform.students;

import com.student.management.platform.global.MasterException;
import com.student.management.platform.global.HtmlParser;
import com.student.management.platform.global.persistence.CustomPersistenceService;
import com.student.management.platform.global.persistence.EntityType;
import com.student.management.platform.global.persistence.ImagePersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
class StudentsService {

  private final CustomPersistenceService<Student> customPersistenceService;
  private static final Logger logger = LoggerFactory.getLogger(StudentsService.class);
  private final StudentRepository studentRepository;
  private final CacheManager cacheManager;
  private final ImagePersistenceService imagePersistenceService;
  private final HtmlParser htmlParser;
  private static final String STUDENT_CACHE = "students";
  private static final String STUDENTS_KEY = "studentsDTO";

  StudentsService(CustomPersistenceService<Student> customPersistenceService, StudentRepository studentRepository,
      CacheManager cacheManager, ImagePersistenceService imagePersistenceService, HtmlParser htmlParser) {
    this.customPersistenceService = customPersistenceService;
    this.studentRepository = studentRepository;
    this.cacheManager = cacheManager;
    this.imagePersistenceService = imagePersistenceService;
    this.htmlParser = htmlParser;
  }

  @SuppressWarnings("unchecked")
  Set<StudentDto> getAllStudents() {
    Cache cache = cacheManager.getCache(STUDENT_CACHE);
    assert cache != null;
    Cache.ValueWrapper valueWrapper = cache.get(STUDENTS_KEY);
    if (valueWrapper != null) {
      return (Set<StudentDto>) valueWrapper.get();
    }
    Set<StudentDto> mastersDB = studentRepository.fetchStudentsDTO();
    cache.put(STUDENTS_KEY, mastersDB);
    return mastersDB;
  }

  void injectNewStudent(StudentDto studentDTO, MultipartFile image) {
    Student student = convertToStudentEntity(studentDTO, image);
    try {
      if (!studentRepository.existsByName(student.getName())) {
        customPersistenceService.persist(student);
        imagePersistenceService.mountImageToVolume(image, EntityType.STUDENT, studentDTO.getName());
        String savedMessage = "Successfully saved new student " + studentDTO.getName();
        logger.info(savedMessage);
        addNewStudentToCache(studentDTO);
      } else {
        throw new MasterException("Student already exists!", new RuntimeException());
      }
    } catch (Exception e) {
      updateExistingStudent(studentDTO, student, e, image);
    }
  }

  @SuppressWarnings("unchecked")
  private void addNewStudentToCache(StudentDto student) {
    Cache cache = cacheManager.getCache(STUDENT_CACHE);
    assert cache != null;
    Cache.ValueWrapper valueWrapper = cache.get(STUDENTS_KEY);
    if (valueWrapper != null) {
      Set<StudentDto> students = (Set<StudentDto>) valueWrapper.get();
      assert students != null;
      students.add(student);
    }
  }

  private void updateExistingStudent(StudentDto studentDTO, Student student, Exception e, MultipartFile image) {
    Integer existentStudentId = studentRepository.findIdByName(studentDTO.getName());
    if (existentStudentId != null) {
      updateStudentEntity(studentDTO, student, existentStudentId, image);
    } else {
      String error = e.getMessage();
      logger.error(error);
      throw new MasterException(error, e);
    }
  }

  private void updateStudentEntity(StudentDto studentDTO, Student student, Integer existentStudentId, MultipartFile image) {
    student.setId(existentStudentId);
    customPersistenceService.update(student);
    imagePersistenceService.mountImageToVolume(image, EntityType.STUDENT, student.getName());
    String successfulUpdate = "Successfully updated " + studentDTO.getName();
    logger.info(successfulUpdate);
    updateStudentsCache(studentDTO);
  }

  @SuppressWarnings("unchecked")
  private void updateStudentsCache(StudentDto student) {
    Cache cache = cacheManager.getCache(STUDENT_CACHE);
    assert cache != null;
    Cache.ValueWrapper valueWrapper = cache.get(STUDENTS_KEY);
    if (valueWrapper != null) {
      Set<StudentDto> students = (Set<StudentDto>) valueWrapper.get();
      Set<StudentDto> updatedStudents = new HashSet<>();
      assert students != null;
      for (StudentDto masterDTO : students) {
        if (!masterDTO.getName().equalsIgnoreCase(student.getName())) {
          updatedStudents.add(masterDTO);
        }
      }
      updatedStudents.add(student);
      cache.put(STUDENTS_KEY, updatedStudents);
    }
  }

  private Student convertToStudentEntity(StudentDto dto, MultipartFile image) {
    String imagePath = "/student/" + dto.getName() + "/" + image.getOriginalFilename();
    dto.setImageUrl(imagePath);
    return new Student(
        null,
        dto.getName(),
        dto.getGender(),
        dto.getMentor(),
        htmlParser.parseHtml(dto.getContent()),
        htmlParser.parseHtml(dto.getSummary()),
        dto.getLinks(),
        imagePath
    );
  }

  void deleteStudentByName(String name) {
    studentRepository.deleteByName(name);
    String deletedMessage = "Successfully deleted student " + name;
    logger.info(deletedMessage);
    deleteStudentFromCache(name);
    imagePersistenceService.unmountImageFromVolume(EntityType.STUDENT, name);
  }

  @SuppressWarnings("unchecked")
  private void deleteStudentFromCache(String name) {
    Cache cache = cacheManager.getCache(STUDENT_CACHE);
    assert cache != null;
    Cache.ValueWrapper valueWrapper = cache.get(STUDENTS_KEY);
    if (valueWrapper != null) {
      Set<StudentDto> students = (Set<StudentDto>) valueWrapper.get();
      assert students != null;
      cache.put(STUDENTS_KEY, students
          .stream()
          .filter(student -> !student.getName().equalsIgnoreCase(name))
          .collect(Collectors.toSet()));
    }
  }
}
