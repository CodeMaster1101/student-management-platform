package com.student.management.platform.students;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
class StudentDto {

  private String name;
  private String gender;
  private String mentor;
  private String content;
  private String summary;
  private Map<String, String> links;
  @Nullable
  private String imageUrl;

  public StudentDto(Student student) {
    this.name = student.getName();
    this.gender = student.getGender();
    this.mentor = student.getMentor();
    this.content = student.getContent();
    this.summary = student.getSummary();
    this.links = student.getLinks();
    this.imageUrl = student.getImageUrl();
  }

  void setImageUrl(@Nullable String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
