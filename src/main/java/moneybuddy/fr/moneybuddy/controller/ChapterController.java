/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterDto;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterWithoutCourses;
import moneybuddy.fr.moneybuddy.service.ChapterService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chapters")
@RequiredArgsConstructor
public class ChapterController {

  private final ChapterService chapterService;

  @GetMapping("")
  public ResponseEntity<Page<ChapterWithoutCourses>> getAllChapters(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "order") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    String token = authHeader.substring(7);
    Page<ChapterWithoutCourses> chapters =
        chapterService.getChapters(token, page, size, sortBy, sortDir);
    return ResponseEntity.status(HttpStatus.OK).body(chapters);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ChapterDto> getChapterById(@PathVariable String id) {
    return ResponseEntity.status(HttpStatus.OK).body(chapterService.getChapter(id));
  }
}
