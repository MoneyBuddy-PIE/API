package moneybuddy.fr.moneybuddy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.service.CourseService;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/chapter/{id}")
    public ResponseEntity<List<Course>> getCourses(
        @PathVariable String id
    ) {
        return courseService.getCourses(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(
        @PathVariable String id
    ) {
        return courseService.getCourse(id);
    }
}
