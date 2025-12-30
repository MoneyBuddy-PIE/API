/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.exception.UserProgressNotFoundException;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.ChapterProgress;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.CourseProgress;
import moneybuddy.fr.moneybuddy.model.Section;
import moneybuddy.fr.moneybuddy.model.SectionProgress;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.UserProgress;
import moneybuddy.fr.moneybuddy.repository.ChapterRepository;
import moneybuddy.fr.moneybuddy.repository.CourseRepository;
import moneybuddy.fr.moneybuddy.repository.UserProgressRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProgressService {

  private final UserProgressRepository userProgressRepository;
  private final ChapterRepository chapterRepository;
  private final CourseRepository courseRepository;

  public void createBasicUserProgress(String subAccountId) {
    UserProgress userProgress =
        UserProgress.builder().chapterProgress(new HashMap<>()).subAccountId(subAccountId).build();

    userProgressRepository.save(userProgress);
  }

  private UserProgress getBySubAccountId(String subAccountId) {
    return userProgressRepository
        .findBySubAccountId(subAccountId)
        .orElseThrow(() -> new UserProgressNotFoundException(subAccountId));
  }

  public void markSectionAsCompleted(SubAccount subAccount, Section section, BigDecimal score) {
    UserProgress userProgress = getBySubAccountId(subAccount.getId());

    String chapterId = section.getChapterId();
    ChapterProgress chapterProgress =
        userProgress
            .getChapterProgress()
            .getOrDefault(
                chapterId,
                ChapterProgress.builder()
                    .chapterId(chapterId)
                    .courseProgress(new HashMap<>())
                    .build());

    String courseId = section.getCourseId();
    CourseProgress courseProgress =
        chapterProgress
            .getCourseProgress()
            .getOrDefault(
                courseId,
                CourseProgress.builder()
                    .courseId(courseId)
                    .chapterId(chapterId)
                    .sectionProgress(new HashMap<>())
                    .build());

    SectionProgress sectionProgress =
        courseProgress
            .getSectionProgress()
            .getOrDefault(
                section.getId(),
                SectionProgress.builder()
                    .sectionId(section.getId())
                    .chapterId(chapterId)
                    .courseId(courseId)
                    .score(section.getMinimumScoreToPass())
                    .build());

    sectionProgress.setCompleted(true);
    sectionProgress.setScore(score);
    courseProgress.getSectionProgress().put(section.getId(), sectionProgress);

    boolean allCompleted =
        courseProgress.getSectionProgress().values().stream()
            .allMatch(SectionProgress::isCompleted);

    if (allCompleted && !courseProgress.isCompleted()) {
      courseProgress.setCompleted(true);
      courseProgress.setCompletedAt(LocalDateTime.now());
    }

    userProgress.getChapterProgress().put(chapterId, chapterProgress);
    chapterProgress.getCourseProgress().put(courseId, courseProgress);
    userProgress.setUpdatedAt(LocalDateTime.now());

    userProgressRepository.save(userProgress);
  }

  public void markCourseAsCompleted(SubAccount subAccount, Course course) {
    UserProgress userProgress = getBySubAccountId(subAccount.getId());
    ChapterProgress chapterProgress = userProgress.getChapterProgress().get(course.getChapterId());

    CourseProgress courseProgress =
        chapterProgress
            .getCourseProgress()
            .getOrDefault(
                course.getId(),
                CourseProgress.builder()
                    .courseId(course.getId())
                    .sectionProgress(new HashMap<>())
                    .build());

    courseProgress.setCompleted(true);
    courseProgress.setCompletedAt(LocalDateTime.now());

    chapterProgress.getCourseProgress().put(course.getId(), courseProgress);
    userProgress.getChapterProgress().put(chapterProgress.getChapterId(), chapterProgress);
    userProgress.setUpdatedAt(LocalDateTime.now());

    userProgressRepository.save(userProgress);

    course.setCompleted(course.getCompleted() + 1);
    courseRepository.save(course);
  }

  public void markChapterAsCompleted(SubAccount subAccount, Chapter chapter) {
    UserProgress userProgress = getBySubAccountId(subAccount.getId());
    ChapterProgress chapterProgress = userProgress.getChapterProgress().get(chapter.getId());

    int courseCompleted = 0;
    for (CourseProgress c : chapterProgress.getCourseProgress().values())
      if (c.isCompleted()) courseCompleted++;

    if (courseCompleted != chapter.getCourses().size()) return;

    chapterProgress.setCompleted(true);
    chapterProgress.setCompletedAt(LocalDateTime.now());
    userProgress.setUpdatedAt(LocalDateTime.now());

    userProgressRepository.save(userProgress);

    chapter.setCompleted(chapter.getCompleted() + 1);
    chapterRepository.save(chapter);
  }
}
