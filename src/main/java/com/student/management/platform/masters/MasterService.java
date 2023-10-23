package com.student.management.platform.masters;

import com.student.management.platform.global.HtmlParser;
import com.student.management.platform.global.MasterException;
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
class MasterService {

  private final CustomPersistenceService<Master> customPersistenceService;
  private final MasterRepository masterRepository;
  private static final Logger logger = LoggerFactory.getLogger(MasterService.class);
  private final CacheManager cacheManager;
  private final ImagePersistenceService imagePersistenceService;
  private final HtmlParser htmlParser;
  private static final String MASTERS_CACHE = "masters";
  private static final String MASTERS_KEY = "mastersDTO";

  MasterService(CustomPersistenceService<Master> customPersistenceService, MasterRepository masterRepository, CacheManager cacheManager,
      ImagePersistenceService imagePersistenceService, HtmlParser htmlParser) {
    this.customPersistenceService = customPersistenceService;
    this.masterRepository = masterRepository;
    this.cacheManager = cacheManager;
    this.imagePersistenceService = imagePersistenceService;
    this.htmlParser = htmlParser;
  }

  @SuppressWarnings("unchecked")
  Set<MasterDto> getCodeMasters() {
    Cache cache = cacheManager.getCache(MASTERS_CACHE);
    assert cache != null;
    Cache.ValueWrapper valueWrapper = cache.get(MASTERS_KEY);
    if (valueWrapper != null) {
      return (Set<MasterDto>) valueWrapper.get();
    }
    Set<MasterDto> mastersDB = masterRepository.findAllSet();
    cache.put(MASTERS_KEY, mastersDB);
    return mastersDB;
  }

  void injectNewMaster(MasterDto codeMasterDTO, MultipartFile image) {
    Master master = convertToCodeMaster(codeMasterDTO, image.getOriginalFilename());
    try {
      persistNewMaster(codeMasterDTO, master, image);
    } catch (Exception e) {
      updateExistentMaster(codeMasterDTO, master, e, image);
    }
  }

  private void persistNewMaster(MasterDto codeMasterDTO, Master master, MultipartFile image) {
    String masterName = master.getName();
    if (!masterRepository.existsByName(masterName)) {
      customPersistenceService.persist(master);
      imagePersistenceService.mountImageToVolume(image, EntityType.MASTER, masterName);
      String createMessage = "Successfully created " + masterName;
      logger.info(createMessage);
      addNewMasterToCache(codeMasterDTO);
    } else {
      throw new MasterException("Master already exists!", new RuntimeException());
    }
  }

  @SuppressWarnings("unchecked")
  private void addNewMasterToCache(MasterDto master) {
    Cache cache = cacheManager.getCache(MASTERS_CACHE);
    assert cache != null;
    Cache.ValueWrapper valueWrapper = cache.get(MASTERS_KEY);
    if (valueWrapper != null) {
      Set<MasterDto> masters = (Set<MasterDto>) valueWrapper.get();
      assert masters != null;
      masters.add(master);
      cache.put(MASTERS_KEY, masters);
    }
  }

  private void updateExistentMaster(MasterDto dto, Master master, Exception e, MultipartFile image) {
    Integer existentMasterId = masterRepository.findIdByName(master.getName());
    if (existentMasterId != null) {
      updateMasterEntity(dto, master, existentMasterId, image);
    } else {
      String error = e.getMessage();
      logger.error(error);
      throw new MasterException(error, e);
    }
  }

  private void updateMasterEntity(MasterDto dto, Master master, Integer existentMasterId, MultipartFile image) {
    master.setId(existentMasterId);
    customPersistenceService.update(master);
    imagePersistenceService.mountImageToVolume(image, EntityType.MASTER, master.getName());
    String updateMessage = "Successfully updated " + master.getName();
    logger.info(updateMessage);
    updateMastersCache(dto);
  }

  @SuppressWarnings("unchecked")
  private void updateMastersCache(MasterDto master) {
    Cache cache = cacheManager.getCache(MASTERS_CACHE);
    assert cache != null;
    Cache.ValueWrapper valueWrapper = cache.get(MASTERS_KEY);
    if (valueWrapper != null) {
      Set<MasterDto> masters = (Set<MasterDto>) valueWrapper.get();
      Set<MasterDto> updatedMasters = new HashSet<>();
      assert masters != null;
      for (MasterDto masterDTO : masters) {
        if (!masterDTO.getName().equalsIgnoreCase(master.getName())) {
          updatedMasters.add(masterDTO);
        }
      }
      updatedMasters.add(master);
      cache.put(MASTERS_KEY, updatedMasters);
    }
  }

  private Master convertToCodeMaster(MasterDto dto, String imageName) {
    String imagePath = "/master/" + dto.getName() + "/" + imageName;
    dto.setImageUrl(imagePath);
    return new Master(
        null,
        dto.getName(),
        dto.getGender(),
        dto.getRole(),
        dto.getQuote(),
        htmlParser.parseHtml(dto.getBio()),
        dto.getLinks(),
        imagePath
        );
  }

  void deleteMasterByName(String name) {
    masterRepository.deleteByName(name);
    String deleteMessage = "Successfully deleted master " + name;
    logger.info(deleteMessage);
    deleteMasterFromCache(name);
    imagePersistenceService.unmountImageFromVolume(EntityType.MASTER, name);
  }

  @SuppressWarnings("unchecked")
  private void deleteMasterFromCache(String name) {
    Cache cache = cacheManager.getCache(MASTERS_CACHE);
    assert cache != null;
    Cache.ValueWrapper valueWrapper = cache.get(MASTERS_KEY);
    if (valueWrapper != null) {
      Set<MasterDto> masters = (Set<MasterDto>) valueWrapper.get();
      assert masters != null;
      cache.put(MASTERS_KEY, masters
          .stream()
          .filter(master -> !master.getName().equalsIgnoreCase(name))
          .collect(Collectors.toSet()));
    }
  }
}
