package moneybuddy.fr.moneybuddy.controller;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.chapter.CreateChapterRequest;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.ChapterWithCourses;
import moneybuddy.fr.moneybuddy.model.ChapterWithoutCourses;
import moneybuddy.fr.moneybuddy.service.ChapterService;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    public ResponseEntity<ChapterWithCourses> getChapterById (
        @PathVariable String id
    ) {
        return chapterService.getChapter(id);
    }

    @PostMapping("")
    public ResponseEntity<Chapter> createChapter(
        @Valid @ModelAttribute CreateChapterRequest req,
        @RequestHeader("Authorization") String authHeader
    ) throws FileUploadException {
        String token = authHeader.substring(7);
        return chapterService.createChapter(token, req);
    }
}