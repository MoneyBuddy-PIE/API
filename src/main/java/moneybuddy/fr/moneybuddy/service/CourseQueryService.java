/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.course.CourseProgressData;
import moneybuddy.fr.moneybuddy.dtos.course.CourseWithProgress;
import moneybuddy.fr.moneybuddy.dtos.course.CourseWithoutSectionsWithProgress;
import moneybuddy.fr.moneybuddy.dtos.section.SectionWithProgress;
import moneybuddy.fr.moneybuddy.exception.CourseNotFoundException;
import moneybuddy.fr.moneybuddy.exception.UserProgressNotFoundException;
import moneybuddy.fr.moneybuddy.model.ChapterProgress;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.CourseProgress;
import moneybuddy.fr.moneybuddy.model.Section;
import moneybuddy.fr.moneybuddy.model.SectionProgress;
import moneybuddy.fr.moneybuddy.model.UserProgress;
import moneybuddy.fr.moneybuddy.repository.CourseRepository;
import moneybuddy.fr.moneybuddy.repository.UserProgressRepository;
import moneybuddy.fr.moneybuddy.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseQueryService {
  private final CourseRepository courseRepository;
  private final UserProgressRepository userProgressRepository;
  private final JwtService jwtService;
  private final Utils utils;

  public Page<CourseWithoutSectionsWithProgress> getCoursesWithProgress(
      String token, String chapterId, int page, int size, String sortBy, String sortDir) {
    String subAccountId = jwtService.extractSubAccountId(token);

    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);
    Page<Course> courses =
        courseRepository
            .findAllByChapterIdAndLockedFalse(chapterId, pageable)
            .orElseThrow(() -> new CourseNotFoundException());

    UserProgress userProgress =
        userProgressRepository
            .findBySubAccountId(subAccountId)
            .orElseThrow(() -> new UserProgressNotFoundException(subAccountId));

    return courses.map(
        course -> {
          CourseProgressData progress = calculateCoursProgress(course, userProgress);

          return CourseWithoutSectionsWithProgress.builder()
              .id(course.getId())
              .chapterId(course.getChapterId())
              .title(course.getTitle())
              .coinReward(course.getCoinReward())
              .readTime(course.getReadTime())
              .imageUrl(course.getImage_url())
              .order(course.getOrder())
              .createdAt(course.getCreatedAt())
              .updatedAt(course.getUpdatedAt())
              .completed(progress.isCompleted())
              .ressource(course.getRessourceAsList())
              .completedCoursesCount(progress.getCompletedCoursesCount())
              .totalCoursesCount(progress.getTotalCoursesCount())
              .progressPercentage(progress.getProgressPercentage())
              .build();
        });
  }

  public CourseWithProgress getCourseWithProgress(String token, String couseId) {
    String subAccountId = jwtService.extractSubAccountId(token);

    Course course =
        courseRepository.findById(couseId).orElseThrow(() -> new CourseNotFoundException(couseId));

    UserProgress userProgress =
        userProgressRepository
            .findBySubAccountId(subAccountId)
            .orElseThrow(() -> new UserProgressNotFoundException(subAccountId));

    CourseProgressData progress = calculateCoursProgress(course, userProgress);

    return CourseWithProgress.builder()
        .id(course.getId())
        .chapterId(course.getChapterId())
        .title(course.getTitle())
        .order(course.getOrder())
        .coinReward(course.getCoinReward())
        .imageUrl(course.getImage_url())
        .readTime(course.getReadTime())
        .locked(course.isLocked())
        .completed(progress.isCompleted())
        .completedCoursesCount(progress.getCompletedCoursesCount())
        .totalCoursesCount(progress.getTotalCoursesCount())
        .progressPercentage(progress.getProgressPercentage())
        .sections(progress.getSections())
        .ressources(course.getRessource())
        .updatedAt(course.getUpdatedAt())
        .createdAt(course.getCreatedAt())
        .build();
  }

  private CourseProgressData calculateCoursProgress(Course course, UserProgress userProgress) {
    ChapterProgress chapterProgress =
        userProgress.getChapterProgress() != null
            ? userProgress.getChapterProgress().get(course.getChapterId())
            : null;

    // Pas de progression du tout
    if (chapterProgress == null
        || chapterProgress.getCourseProgress() == null
        || chapterProgress.getCourseProgress().isEmpty()) {

      Map<String, Section> courseSections = course.getSections();
      int totalSections = courseSections != null ? courseSections.size() : 0;

      Map<String, SectionWithProgress> sections = new HashMap<>();
      if (courseSections != null && !courseSections.isEmpty()) {
        sections =
            courseSections.entrySet().stream()
                .map(
                    entry -> {
                      Section sec = entry.getValue();
                      return SectionWithProgress.builder()
                          .id(sec.getId())
                          .courseId(sec.getCourseId())
                          .chapterId(sec.getChapterId())
                          .title(sec.getTitle())
                          .markdownContent(sec.getMarkdownContent())
                          .createdAt(sec.getCreatedAt())
                          .updatedAt(sec.getUpdatedAt())
                          .completed(false)
                          .quiz(sec.getQuiz())
                          .build();
                    })
                .collect(Collectors.toMap(SectionWithProgress::getId, s -> s));
      }

      return CourseProgressData.builder()
          .completed(false)
          .completedCoursesCount(0)
          .totalCoursesCount(totalSections)
          .progressPercentage(0)
          .sections(sections)
          .build();
    }

    // Il y a une progression
    CourseProgress courseProgress = chapterProgress.getCourseProgress().get(course.getId());

    // Vérifier que course.getSections() n'est pas null
    Map<String, Section> courseSections = course.getSections();

    if (courseSections == null || courseSections.isEmpty()) {
      return CourseProgressData.builder()
          .completed(false)
          .completedCoursesCount(0)
          .totalCoursesCount(0)
          .progressPercentage(0)
          .sections(new HashMap<>())
          .build();
    }

    boolean completed = courseProgress != null && courseProgress.isCompleted();
    int totalSections = courseSections.size();
    int completedSections = 0;

    if (courseProgress != null && courseProgress.getSectionProgress() != null) {
      completedSections =
          (int)
              courseProgress.getSectionProgress().values().stream()
                  .filter(SectionProgress::isCompleted)
                  .count();
    }

    int progressPercentage = totalSections > 0 ? (completedSections * 100 / totalSections) : 0;

    // Maintenant on sait que courseSections n'est pas null
    Map<String, SectionWithProgress> sections =
        courseSections.entrySet().stream()
            .map(entry -> calculateSectionProgress(entry.getValue(), courseProgress))
            .collect(Collectors.toMap(SectionWithProgress::getId, section -> section));

    return CourseProgressData.builder()
        .completed(completed)
        .completedCoursesCount(completedSections)
        .totalCoursesCount(totalSections)
        .progressPercentage(progressPercentage)
        .sections(sections)
        .build();
  }

  private SectionWithProgress calculateSectionProgress(
      Section section, CourseProgress courseProgress) {
    boolean sectionCompleted = false;

    if (courseProgress != null && courseProgress.getSectionProgress() != null) {
      SectionProgress progress = courseProgress.getSectionProgress().get(section.getId());
      sectionCompleted = progress != null && progress.isCompleted();
    }

    return SectionWithProgress.builder()
        .id(section.getId())
        .chapterId(section.getChapterId())
        .completed(sectionCompleted)
        .courseId(section.getCourseId())
        .createdAt(section.getCreatedAt())
        .updatedAt(section.getUpdatedAt())
        .markdownContent(section.getMarkdownContent())
        .title(section.getTitle())
        .quiz(section.getQuiz())
        .build();
  }
}
