/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.section.CreateSectionRequest;
import moneybuddy.fr.moneybuddy.dtos.section.UpdateSectionRequest;
import moneybuddy.fr.moneybuddy.exception.CourseNotFoundException;
import moneybuddy.fr.moneybuddy.exception.SectionNotFoundException;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.Section;
import moneybuddy.fr.moneybuddy.repository.CourseRepository;
import moneybuddy.fr.moneybuddy.repository.QuizRepository;
import moneybuddy.fr.moneybuddy.repository.SectionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectionService {

  private final SectionRepository sectionRepository;
  private final CourseRepository courseRepository;
  private final QuizRepository quizRepository;

  public Section getById(String id) {
    return sectionRepository.findById(id).orElseThrow(() -> new SectionNotFoundException(id));
  }

  public Course getCourse(String courseId) {
    return courseRepository
        .findById(courseId)
        .orElseThrow(() -> new CourseNotFoundException(courseId));
  }

  public Section createSection(CreateSectionRequest req) {
    Course course = getCourse(req.getCourseId());

    Section section =
        Section.builder()
            .chapterId(course.getChapterId())
            .courseId(course.getId())
            .title(req.getTitle())
            .markdownContent(req.getMarkdownContent())
            .minimumScoreToPass(req.getMinimumScoreToPass())
            .build();
    section = sectionRepository.save(section);

    course.getSections().put(section.getId(), section);
    courseRepository.save(course);

    return section;
  }

  public Section updateSection(String sectionId, UpdateSectionRequest req) {
    Section section = getById(sectionId);

    if (req.getTitle() != null) section.setTitle(req.getTitle());
    if (req.getMarkdownContent() != null) section.setMarkdownContent(req.getMarkdownContent());
    if (req.getMinimumScoreToPass() != null)
      section.setMinimumScoreToPass(req.getMinimumScoreToPass());

    section.setUpdatedAt(LocalDateTime.now());
    section = sectionRepository.save(section);
    return section;
  }

  public void deleteSection(String sectionId) {
    Section section = getById(sectionId);
    Course course = getCourse(section.getCourseId());

    course.getSections().remove(sectionId);
    courseRepository.save(course);

    quizRepository.deleteAllBySectionId(sectionId);
    sectionRepository.delete(section);
  }
}
