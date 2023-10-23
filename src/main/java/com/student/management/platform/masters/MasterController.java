package com.student.management.platform.masters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/masters")
class MasterController {

  private final MasterService masterService;

  MasterController(MasterService masterService) {
    this.masterService = masterService;
  }

  @GetMapping
  public Set<MasterDto> getCodeMasters() {
    return masterService.getCodeMasters();
  }

  @PostMapping
  public ResponseEntity<String> injectNewMaster(
      @RequestParam("image") MultipartFile image,
      @RequestParam("master") String dto) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      MasterDto masterDTO = objectMapper.readValue(dto, MasterDto.class);
      masterService.injectNewMaster(masterDTO, image);
      return new ResponseEntity<>("Master created/updated successfully", HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Unable to create new master", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/{name}")
  public ResponseEntity<String> deleteMaster(@PathVariable("name") String name) {
    try {
      masterService.deleteMasterByName(name);
      return new ResponseEntity<>("Master deleted successfully", HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Unable to delete master", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
