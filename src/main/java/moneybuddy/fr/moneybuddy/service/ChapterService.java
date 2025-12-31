/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterDto;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterWithoutCourses;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterWithoutCoursesForAdmin;
import moneybuddy.fr.moneybuddy.dtos.chapter.CreateChapterRequest;
import moneybuddy.fr.moneybuddy.exception.ChapterNotFound;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.Course;
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
      int page, int size, String sortBy, String sortDir) {
    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);
    Page<Chapter> chapters = chapterRepository.findAll(pageable);
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
            .level(req.getLevel())
            .order(req.getOrder())
            .image_url(image_url)
            .locked(true)
            .coinReward(req.getCoinReward())
            .subAccountRole(req.getSubAccountRole())
            .accountId(accountId)
            .createdAt(LocalDateTime.now())
            .build();

    return chapterRepository.save(chapter);
  }

  public void deleteChapter(String id) {
    ChapterDto chapter = getChapter(id);
    cloudflareService.remove(chapter.getImage_url());
    chapterRepository.deleteById(id);
  }
}
