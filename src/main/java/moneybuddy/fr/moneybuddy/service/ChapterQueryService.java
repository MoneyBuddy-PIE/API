/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterProgressData;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterWithProgress;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterWithoutCoursesWithProgress;
import moneybuddy.fr.moneybuddy.exception.ChapterNotFound;
import moneybuddy.fr.moneybuddy.exception.UserProgressNotFoundException;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.ChapterProgress;
import moneybuddy.fr.moneybuddy.model.CourseProgress;
import moneybuddy.fr.moneybuddy.model.UserProgress;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.repository.ChapterRepository;
import moneybuddy.fr.moneybuddy.repository.UserProgressRepository;
import moneybuddy.fr.moneybuddy.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChapterQueryService {

  private final JwtService jwtService;
  private final Utils utils;
  private final ChapterRepository chapterRepository;
  private final UserProgressRepository userProgressRepository;

  public Page<ChapterWithoutCoursesWithProgress> getChaptersWithProgress(
      String token, int page, int size, String sortBy, String sortDir) {

    SubAccountRole subAccountRole = jwtService.extractSubAccountRole(token);
    subAccountRole =
        SubAccountRole.OWNER.equals(subAccountRole) ? SubAccountRole.PARENT : subAccountRole;

    String subAccountId = jwtService.extractSubAccountId(token);

    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);
    Page<Chapter> chapters =
        chapterRepository.findAllBySubAccountRoleAndLockedFalse(subAccountRole, pageable);

    UserProgress userProgress =
        userProgressRepository
            .findBySubAccountId(subAccountId)
            .orElseThrow(() -> new UserProgressNotFoundException(subAccountId));

    return chapters.map(
        chapter -> {
          ChapterProgressData progress = calculateChapterProgress(chapter, userProgress);
          return ChapterWithoutCoursesWithProgress.builder()
              .id(chapter.getId())
              .title(chapter.getTitle())
              .description(chapter.getDescription())
              .level(chapter.getLevel())
              .order(chapter.getOrder())
              .coinReward(chapter.getCoinReward())
              .imageUrl(chapter.getImage_url())
              .locked(chapter.isLocked())
              .completed(progress.isCompleted())
              .completedCoursesCount(progress.getCompletedCoursesCount())
              .totalCoursesCount(progress.getTotalCoursesCount())
              .progressPercentage(progress.getProgressPercentage())
              .createdAt(chapter.getCreatedAt())
              .updatedAt(chapter.getUpdatedAt())
              .build();
        });
  }

  public ChapterWithProgress getChapterWithProgress(String token, String chapterId) {
    String subAccountId = jwtService.extractSubAccountId(token);
    SubAccountRole subAccountRole = jwtService.extractSubAccountRole(token);
    subAccountRole =
        SubAccountRole.OWNER.equals(subAccountRole) ? SubAccountRole.PARENT : subAccountRole;

    Chapter chapter =
        chapterRepository.findById(chapterId).orElseThrow(() -> new ChapterNotFound(chapterId));

    UserProgress userProgress =
        userProgressRepository
            .findBySubAccountId(subAccountId)
            .orElseThrow(() -> new UserProgressNotFoundException(subAccountId));

    ChapterProgressData progress = calculateChapterProgress(chapter, userProgress);

    return ChapterWithProgress.builder()
        .id(chapter.getId())
        .title(chapter.getTitle())
        .description(chapter.getDescription())
        .level(chapter.getLevel())
        .order(chapter.getOrder())
        .coinReward(chapter.getCoinReward())
        .imageUrl(chapter.getImage_url())
        .locked(chapter.isLocked())
        .completed(progress.isCompleted())
        .completedCoursesCount(progress.getCompletedCoursesCount())
        .totalCoursesCount(progress.getTotalCoursesCount())
        .progressPercentage(progress.getProgressPercentage())
        .courses(chapter.getCourses())
        .updatedAt(chapter.getUpdatedAt())
        .createdAt(chapter.getCreatedAt())
        .build();
  }

  private ChapterProgressData calculateChapterProgress(Chapter chapter, UserProgress userProgress) {
    ChapterProgress chapterProgress = userProgress.getChapterProgress().get(chapter.getId());

    boolean completed = chapterProgress != null && chapterProgress.isCompleted();
    int totalCourses = chapter.getCourses().size();
    int completedCourses = 0;

    if (chapterProgress != null && chapterProgress.getCourseProgress() != null) {
      completedCourses =
          (int)
              chapterProgress.getCourseProgress().values().stream()
                  .filter(CourseProgress::isCompleted)
                  .count();
    }

    int progressPercentage = totalCourses > 0 ? (completedCourses * 100 / totalCourses) : 0;

    return ChapterProgressData.builder()
        .completed(completed)
        .completedCoursesCount(completedCourses)
        .totalCoursesCount(totalCourses)
        .progressPercentage(progressPercentage)
        .build();
  }
}
