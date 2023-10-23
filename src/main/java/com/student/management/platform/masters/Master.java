package com.student.management.platform.masters;

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
@Table(name = "master")
class Master {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(unique = true)
  private String name;

  private String gender;

  private String role;

  private String quote;

  @Column(columnDefinition = "text")
  private String bio;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "master_social_links", joinColumns = @JoinColumn(name = "id"))
  @MapKeyColumn(name = "name")
  @Column(name = "url")
  private Map<String, String> links;
  private String imageUrl;
}
