/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.course.CourseDto;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
  private final CourseService courseService;

  @GetMapping("/chapter/{id}")
  public ResponseEntity<List<CourseDto>> getCourses(@PathVariable String id) {
    return courseService.getCoursesByChapterId(id);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Course> getCourse(@PathVariable String id) {
    return ResponseEntity.status(HttpStatus.FOUND).body(courseService.getById(id));
  }
}
