package com.student.management.platform.masters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
class MasterDto {

  private String name;
  private String gender;
  private String role;
  private String quote;
  private String bio;
  private Map<String, String> links;

  @Nullable
  private String imageUrl;

  public MasterDto(Master codeMaster) {
    this.name = codeMaster.getName();
    this.gender = codeMaster.getGender();
    this.role = codeMaster.getRole();
    this.quote = codeMaster.getQuote();
    this.bio = codeMaster.getBio();
    this.links = codeMaster.getLinks();
    this.imageUrl = codeMaster.getImageUrl();
  }

  void setImageUrl(@Nullable String imageUrl) {
    this.imageUrl = imageUrl;
  }

}
