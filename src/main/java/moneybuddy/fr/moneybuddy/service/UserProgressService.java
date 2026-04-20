/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.exception.CourseNotFoundException;
import moneybuddy.fr.moneybuddy.exception.UserProgressNotFoundException;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.ChapterProgress;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.CourseProgress;
import moneybuddy.fr.moneybuddy.model.Section;
import moneybuddy.fr.moneybuddy.model.SectionProgress;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.UserProgress;
import moneybuddy.fr.moneybuddy.model.enums.CompletedCourse;
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

  public void createBasicUserProgress(SubAccount subAccount) {
    UserProgress userProgress =
        UserProgress.builder()
            .chapterProgress(new HashMap<>())
            .subAccountId(subAccount.getId())
            .accountId(subAccount.getAccountId())
            .build();

    userProgressRepository.save(userProgress);
  }

  private UserProgress getBySubAccountId(String subAccountId) {
    return userProgressRepository
        .findBySubAccountId(subAccountId)
        .orElseThrow(() -> new UserProgressNotFoundException(subAccountId));
  }

  private boolean completeIfAllSectionsFinished(Course course, CourseProgress courseProgress) {
    boolean allSectionsCompleted =
        course.getSections().values().stream()
            .allMatch(
                sec -> {
                  SectionProgress sp = courseProgress.getSectionProgress().get(sec.getId());
                  return sp != null && sp.isCompleted();
                });

    if (allSectionsCompleted && !courseProgress.isCompleted()) {
      courseProgress.setCompleted(true);
      courseProgress.setCompletedAt(LocalDateTime.now());
    }

    return allSectionsCompleted;
  }

  public CompletedCourse markSectionAsCompleted(
      SubAccount subAccount, Section section, BigDecimal score) {
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
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new CourseNotFoundException(courseId));

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
                    .build());

    if (sectionProgress.isCompleted()) return CompletedCourse.ALREADY_COMPLETED;

    sectionProgress.setCompleted(true);
    sectionProgress.setCompletedAt(LocalDateTime.now());
    sectionProgress.setScore(score);

    courseProgress.getSectionProgress().put(section.getId(), sectionProgress);
    completeIfAllSectionsFinished(course, courseProgress);

    chapterProgress.getCourseProgress().put(courseId, courseProgress);
    userProgress.getChapterProgress().put(chapterId, chapterProgress);
    userProgress.setUpdatedAt(LocalDateTime.now());

    userProgressRepository.save(userProgress);
    return CompletedCourse.COMPLETED;
  }

  public CompletedCourse markCourseAsCompleted(SubAccount subAccount, Course course) {
    UserProgress userProgress = getBySubAccountId(subAccount.getId());

    String chapterId = course.getChapterId();
    ChapterProgress chapterProgress =
        userProgress
            .getChapterProgress()
            .getOrDefault(
                chapterId,
                ChapterProgress.builder()
                    .chapterId(chapterId)
                    .courseProgress(new HashMap<>())
                    .build());

    CourseProgress courseProgress =
        chapterProgress
            .getCourseProgress()
            .getOrDefault(
                course.getId(),
                CourseProgress.builder()
                    .courseId(course.getId())
                    .chapterId(chapterId)
                    .sectionProgress(new HashMap<>())
                    .build());

    if (courseProgress.isCompleted()) return CompletedCourse.ALREADY_COMPLETED;

    boolean allSectionsCompleted = completeIfAllSectionsFinished(course, courseProgress);

    if (!allSectionsCompleted) return CompletedCourse.NOT_ENOUGH_SECTION_COMPLETED;

    chapterProgress.getCourseProgress().put(course.getId(), courseProgress);
    userProgress.getChapterProgress().put(chapterId, chapterProgress);
    userProgress.setUpdatedAt(LocalDateTime.now());

    userProgressRepository.save(userProgress);

    course.setCompleted(course.getCompleted() + 1);
    courseRepository.save(course);
    return CompletedCourse.COMPLETED;
  }

  public void markChapterAsCompleted(SubAccount subAccount, Chapter chapter) {
    UserProgress userProgress = getBySubAccountId(subAccount.getId());
    ChapterProgress chapterProgress =
        userProgress
            .getChapterProgress()
            .getOrDefault(
                chapter.getId(),
                ChapterProgress.builder()
                    .chapterId(chapter.getId())
                    .courseProgress(new HashMap<>())
                    .build());

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
