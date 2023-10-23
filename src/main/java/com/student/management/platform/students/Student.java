package com.student.management.platform.students;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student")
class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(unique = true)
  private String name;

  private String gender;

  private String mentor;
  
  @Column(columnDefinition = "text")
  private String content;

  private String summary;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "student_social_links", joinColumns = @JoinColumn(name = "id"))
  @MapKeyColumn(name = "name")
  @Column(name = "url")
  private Map<String, String> links;

  private String imageUrl;
}
