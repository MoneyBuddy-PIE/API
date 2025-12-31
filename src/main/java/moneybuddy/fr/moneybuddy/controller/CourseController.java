/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.course.CourseWithProgress;
import moneybuddy.fr.moneybuddy.dtos.course.CourseWithoutSectionsWithProgress;
import moneybuddy.fr.moneybuddy.service.CourseQueryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
  private final CourseQueryService courseQueryService;

  @GetMapping("/chapter/{chapterId}")
  public ResponseEntity<Page<CourseWithoutSectionsWithProgress>> getCourses(
      @RequestHeader("Authorization") String authHeader,
      @PathVariable String chapterId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "order") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    String token = authHeader.substring(7);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            courseQueryService.getCoursesWithProgress(
                token, chapterId, page, size, sortBy, sortDir));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CourseWithProgress> getCourse(
      @RequestHeader("Authorization") String authHeader, @PathVariable String id) {
    String token = authHeader.substring(7);
    return ResponseEntity.status(HttpStatus.OK)
        .body(courseQueryService.getCourseWithProgress(token, id));
  }
}
