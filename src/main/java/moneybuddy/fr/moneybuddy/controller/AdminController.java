package moneybuddy.fr.moneybuddy.controller;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.ResponseDto;
import moneybuddy.fr.moneybuddy.dtos.chapter.CreateChapterRequest;
import moneybuddy.fr.moneybuddy.dtos.course.CreateCourseRequest;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.ChapterWithoutCourses;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.service.ChapterService;
import moneybuddy.fr.moneybuddy.service.CourseService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ChapterService chapterService;
    private final CourseService courseService;

    @PostMapping("/chapters")
    public ResponseEntity<Chapter> createChapter(
        @Valid @RequestBody CreateChapterRequest request,
        @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        return chapterService.createChapter(token, request);
    }

    @GetMapping("/chapters")
    public ResponseEntity<Page<ChapterWithoutCourses>> getAllChapters (
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "order") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return chapterService.getAllChapters(page, size, sortBy, sortDir);
    }



    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(
        @Valid @RequestBody CreateCourseRequest request,
        @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        return courseService.createCourse(token, request);
    }

    @DeleteMapping("/courses")
    public ResponseEntity<ResponseDto> deleteCourse(
        @PathVariable String id
    ) {
        return courseService.deleteCourse(id);
    }
}