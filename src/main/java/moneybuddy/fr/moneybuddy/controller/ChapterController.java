package moneybuddy.fr.moneybuddy.controller;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.model.ChapterWithoutCourses;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.service.ChapterService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @GetMapping("")
    public ResponseEntity<Page<ChapterWithoutCourses>> getAllChapters (
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "order") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        String token = authHeader.substring(7);
        return chapterService.getChapters(token, page, size, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Course>> getChapterById (
        @PathVariable String id
    ) {
        return chapterService.getChapter(id);
    }
}