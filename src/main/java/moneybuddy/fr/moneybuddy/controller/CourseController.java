package moneybuddy.fr.moneybuddy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.CourseRequest;
import moneybuddy.fr.moneybuddy.dtos.subAccountCourse.SubAccountCourseComplete;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.service.CourseService;
import moneybuddy.fr.moneybuddy.utils.ValidatorResult;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    
    private final CourseService service;
    private final ValidatorResult validatorResult;

    @PostMapping("")
    public ResponseEntity<AuthResponse> createCourse(
        @Valid @RequestBody CourseRequest request,
        @RequestHeader("Authorization") String authHeader,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return validatorResult.returnErrorMessage(bindingResult);
        }

        String token = authHeader.substring(7);
        return service.createCourse(request, token);
    }

    @GetMapping("")
    public ResponseEntity<List<Course>> getCourses(
        @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        return service.getCourses(token);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(
        @PathVariable String id
    ) {
         return service.getCourse(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AuthResponse> deleteCourse(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable String id
    ) {

        String token = authHeader.substring(7);
        return service.deleteCourse(token, id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthResponse> modifyCourse(
        @Valid @RequestBody CourseRequest request,
        @RequestHeader("Authorization") String authHeader,
        @PathVariable String id
    ) {
        String token = authHeader.substring(7);
        return service.modifyCourse(request, token, id);
    }

    @PutMapping("/complete/{id}")
    public ResponseEntity<AuthResponse> completeCourseReadTime(
        @Valid @RequestBody SubAccountCourseComplete request,
        @RequestHeader("Authorization") String authHeader,
        @PathVariable String id,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return validatorResult.returnErrorMessage(bindingResult);
        }

        String token = authHeader.substring(7);
        return service.completeCourse(request, token, id);
    }
}
