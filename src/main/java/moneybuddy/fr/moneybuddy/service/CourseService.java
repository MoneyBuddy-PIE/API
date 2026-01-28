/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.course.CreateCourseRequest;
import moneybuddy.fr.moneybuddy.dtos.course.UpdateCourseRequest;
import moneybuddy.fr.moneybuddy.exception.ChapterNotFound;
import moneybuddy.fr.moneybuddy.exception.CourseNotFoundException;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.repository.ChapterRepository;
import moneybuddy.fr.moneybuddy.repository.CourseRepository;
import moneybuddy.fr.moneybuddy.repository.QuizRepository;
import moneybuddy.fr.moneybuddy.repository.RessourceRepository;
import moneybuddy.fr.moneybuddy.repository.SectionRepository;
import moneybuddy.fr.moneybuddy.utils.Utils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

  private final CourseRepository courseRepository;
  private final ChapterService chapterService;
  private final ChapterRepository chapterRepository;
  private final RessourceRepository ressourceRepository;
  private final SectionRepository sectionRepository;
  private final QuizRepository quizRepository;
  private final CloudflareService cloudflareService;
  private final JwtService jwtService;
  private final Utils utils;

  public Course getById(String id) {
    return courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
  }

  public Chapter getChapter(String chapterId) {
    return chapterRepository.findById(chapterId).orElseThrow(() -> new ChapterNotFound(chapterId));
  }

  public Page<Course> getAllCourses(int page, int size, String sortBy, String sortDir) {
    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);
    Page<Course> courses = courseRepository.findAll(pageable);

    return courses;
  }

  public void deleteCourse(String courseId) {
    Course course = getById(courseId);
    Chapter chapter = getChapter(course.getChapterId());

    chapter.getCourses().remove(courseId);
    chapterRepository.save(chapter);

    quizRepository.deleteAllByCourseId(courseId);
    ressourceRepository.deleteAllByCourseId(courseId);
    sectionRepository.deleteAllByCourseId(courseId);

    cloudflareService.remove(course.getImage_url());
    courseRepository.delete(course);
  }

  public Course createCourse(String token, CreateCourseRequest req)
      throws FileUploadException, JsonMappingException, JsonProcessingException {
    String accountId = jwtService.extractAccountId(token);
    Chapter chapter = chapterService.getTotalChapter(req.getChapterId());

    Course course =
        Course.builder()
            .accountId(accountId)
            .chapterId(req.getChapterId())
            .title(req.getTitle())
            .readTime(req.getReadTime())
            .order(req.getOrder())
            .locked(true)
            .build();

    String image_url = cloudflareService.uploadImage(req.getFile());
    course.setImage_url(image_url);

    courseRepository.save(course);
    chapterService.addCourseToChapter(chapter, course);

    return course;
  }

  public Course updateCourse(String courseId, UpdateCourseRequest req)
      throws FileUploadException, JsonMappingException, JsonProcessingException {
    Course course = getById(courseId);

    course.setReadTime(Optional.ofNullable(req.getReadTime()).orElse(course.getReadTime()));
    course.setOrder(Optional.ofNullable(req.getOrder()).orElse(course.getOrder()));
    course.setLocked(Optional.ofNullable(req.isLocked()).orElse(course.isLocked()));

    if (!req.getTitle().isEmpty()) course.setTitle(req.getTitle());

    if (req.getFile().getSize() > 0) {
      String image_url = cloudflareService.uploadImage(req.getFile());
      cloudflareService.remove(course.getImage_url());
      course.setImage_url(image_url);
    }

    courseRepository.save(course);
    return course;
  }
}
