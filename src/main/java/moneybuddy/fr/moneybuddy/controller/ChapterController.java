package moneybuddy.fr.moneybuddy.controller;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.chapter.CreateChapterRequest;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.service.ChapterService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @GetMapping("")
    public ResponseEntity<Page<Chapter>> getAllChapters (
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "order") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return chapterService.getChapters(page, size, sortBy, sortDir);
    }

    @GetMapping("/{chapterId}")
    public ResponseEntity<Chapter> getChapterById (
        @RequestHeader("Authorization") String authHeader,
        @PathVariable String chapterId
    ) {
        String token = authHeader.substring(7);
        return chapterService.getChapter(token, chapterId);
    }

    @PostMapping("")
    public ResponseEntity<Chapter> createChapter (
        @RequestHeader("Authorization") String authHeader,
        @Valid @RequestBody CreateChapterRequest req,
        BindingResult bindingResult
    ) {
        String token = authHeader.substring(7);
        return chapterService.createChapter(token, req);
    }
}