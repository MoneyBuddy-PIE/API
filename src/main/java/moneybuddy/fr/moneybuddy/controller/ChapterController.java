/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterWithProgress;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterWithoutCoursesWithProgress;
import moneybuddy.fr.moneybuddy.service.ChapterQueryService;
import moneybuddy.fr.moneybuddy.service.ChapterService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chapters")
@RequiredArgsConstructor
public class ChapterController {

  private final ChapterQueryService chapterQueryService;
  public final ChapterService chapterService;

  @GetMapping("")
  public ResponseEntity<Page<ChapterWithoutCoursesWithProgress>> getAllChapters(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(defaultValue = "*") String category,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "order") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    String token = authHeader.substring(7);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            chapterQueryService.getChaptersWithProgress(
                token, category, page, size, sortBy, sortDir));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ChapterWithProgress> getChapterById(
      @RequestHeader("Authorization") String authHeader, @PathVariable String id) {

    String token = authHeader.substring(7);
    return ResponseEntity.status(HttpStatus.OK)
        .body(chapterQueryService.getChapterWithProgress(token, id));
  }
}
