package moneybuddy.fr.moneybuddy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.CourseRequest;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.service.CourseService;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    
    private final CourseService service;

    @PostMapping("")
    public ResponseEntity<AuthResponse> createCourse(
        @Valid @RequestBody CourseRequest request,
        @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        return service.createCourse(request, token);
        // return service.createCourse(request);
    }

    // @GetMapping("")
    // public ResponseEntity<List<Course>> getTasks(
    //     @RequestHeader("Authorization") String authHeader,
    //     @RequestParam(required = false) String source
    // ) {
    //     String token = authHeader.substring(7);
    //     return service.getCourse(token, source);
    // }

    // @GetMapping("/{id}")
    // public ResponseEntity<Course> getTask(
    //     @PathVariable String id
    // ) {
    //     return service.getTask(id);
    // }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<AuthResponse> deleteCourse(
    //     @RequestHeader("Authorization") String authHeader,
    //     @PathVariable String id
    // ) {
    //     String token = authHeader.substring(7);
    //     return service.deleteCourse(token, id);
    // }

    // @PutMapping("/{id}")
    // public ResponseEntity<Course> modifyCourse(
    //     @Valid @RequestBody CourseRequest request,
    //     @RequestHeader("Authorization") String authHeader,
    //     @PathVariable String id
    // ) {
    //     String token = authHeader.substring(7);
    //     return service.modifyCourse(request, token, id);
    // }

    // @PutMapping("/complete/{id}")
    // public ResponseEntity<AuthResponse> completeCourseReadTime(
    //     @RequestHeader("Authorization") String authHeader,
    //     @PathVariable String id
    // ) {
    //     String token = authHeader.substring(7);
    //     return service.completeReadTimeAmount(token, id);
    // }
}
