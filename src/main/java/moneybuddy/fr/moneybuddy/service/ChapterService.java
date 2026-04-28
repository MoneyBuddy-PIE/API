/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterDto;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterWithoutCourses;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterWithoutCoursesForAdmin;
import moneybuddy.fr.moneybuddy.dtos.chapter.CreateChapterRequest;
import moneybuddy.fr.moneybuddy.dtos.chapter.UpdateChapterRequest;
import moneybuddy.fr.moneybuddy.exception.ChapterNotFound;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.enums.ChapterCategory;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.repository.ChapterRepository;
import moneybuddy.fr.moneybuddy.utils.Utils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChapterService {

  private final ChapterRepository chapterRepository;
  private final CloudflareService cloudflareService;
  private final JwtService jwtService;
  private final Utils utils;

  public List<ChapterCategory> getChapterCategories() {
    return List.of(ChapterCategory.values());
  }

  public void addCourseToChapter(Chapter chapter, Course course) {
    chapter.getCourses().put(course.getId(), course);
    chapterRepository.save(chapter);
  }

  public Page<ChapterWithoutCourses> getChapters(
      String token, int page, int size, String sortBy, String sortDir) {
    SubAccountRole subAccountRole = jwtService.extractSubAccountRole(token);
    subAccountRole =
        SubAccountRole.OWNER.equals(subAccountRole) ? SubAccountRole.PARENT : subAccountRole;

    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);
    Page<Chapter> chapters =
        chapterRepository.findAllBySubAccountRoleAndLockedFalse(subAccountRole, pageable);

    return chapters.map(ChapterWithoutCourses::from);
  }

  public Page<ChapterWithoutCoursesForAdmin> getAllChapters(
      String category, int page, int size, String sortBy, String sortDir) {
    Pageable pageable;
    try {
      pageable = utils.pagination(page, size, sortBy, sortDir);
    } catch (Exception e) {
      pageable = utils.pagination(page, size, "createdAt", sortDir != null ? sortDir : "asc");
    }

    Page<Chapter> chapters;
    if (category == null || category.equals("*")) {
      chapters = chapterRepository.findAllWithoutCourses(pageable);
    } else {
      chapters = chapterRepository.findAllByCategoryContaining(category, pageable);
    }

    return chapters.map(ChapterWithoutCoursesForAdmin::from);
  }

  public ChapterDto getChapter(String id) {
    Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new ChapterNotFound(id));
    return ChapterDto.from(chapter);
  }

  public Chapter getTotalChapter(String id) {
    return chapterRepository.findById(id).orElseThrow(() -> new ChapterNotFound(id));
  }

  public Chapter createChapter(String token, CreateChapterRequest req) throws FileUploadException {
    String accountId = jwtService.extractAccountId(token);
    String image_url = cloudflareService.uploadImage(req.getFile());

    Chapter chapter =
        Chapter.builder()
            .title(req.getTitle())
            .description(req.getDescription())
            .image_url(image_url)
            .locked(true)
            .subAccountRole(req.getSubAccountRole())
            .accountId(accountId)
            .createdAt(LocalDateTime.now())
            .category(req.getCategory())
            .build();

    if (SubAccountRole.CHILD.equals(req.getSubAccountRole())) {
      chapter.setOrder(req.getOrder());
      chapter.setLevel(req.getLevel());
      chapter.setCoinReward(req.getCoinReward());
    }

    return chapterRepository.save(chapter);
  }

  public void deleteChapter(String id) {
    ChapterDto chapter = getChapter(id);
    cloudflareService.remove(chapter.getImage_url());
    chapterRepository.deleteById(id);
  }

  public Chapter updateCourse(String chapterId, UpdateChapterRequest req)
      throws FileUploadException, JsonMappingException, JsonProcessingException {
    Chapter chapter = getTotalChapter(chapterId);

    chapter.setLocked(Optional.ofNullable(req.isLocked()).orElse(chapter.isLocked()));

    if (!req.getTitle().isEmpty()) chapter.setTitle(req.getTitle());
    if (!req.getDescription().isEmpty()) chapter.setDescription(req.getTitle());
    if (!req.getCategory().isEmpty()) chapter.setCategory(req.getCategory());
    if (req.getSubAccountRole() != null) chapter.setSubAccountRole(req.getSubAccountRole());

    if (req.getFile() != null && req.getFile().getSize() > 0) {
      String image_url = cloudflareService.uploadImage(req.getFile());
      cloudflareService.remove(chapter.getImage_url());
      chapter.setImage_url(image_url);
    }

    if (req.getSubAccountRole().equals(SubAccountRole.CHILD)) {
      chapter.setOrder(Optional.ofNullable(req.getOrder()).orElse(chapter.getOrder()));
      chapter.setLevel(Optional.ofNullable(req.getLevel()).orElse(chapter.getLevel()));
      chapter.setCoinReward(
          Optional.ofNullable(req.getCoinReward()).orElse(chapter.getCoinReward()));
    }

    return chapterRepository.save(chapter);
  }
}
