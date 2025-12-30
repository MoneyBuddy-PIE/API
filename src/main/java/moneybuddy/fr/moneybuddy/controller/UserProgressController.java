/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.ResponseDto;
import moneybuddy.fr.moneybuddy.dtos.course.CompleteCourse;
import moneybuddy.fr.moneybuddy.dtos.section.CompleteSection;
import moneybuddy.fr.moneybuddy.service.ProgressOrchestratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class UserProgressController {
  private final ProgressOrchestratorService progressOrchestratorService;

  @PutMapping("/course/{id}")
  public ResponseEntity<ResponseDto> completeCourse(
      @RequestHeader("Authorization") String authHeader,
      @PathVariable String courseId,
      @Valid @RequestBody CompleteCourse req) {
    String token = authHeader.substring(7);
    progressOrchestratorService.completeCourse(token, req.getChapterId(), courseId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PutMapping("/section/{id}")
  public ResponseEntity<ResponseDto> completeCourse(
      @RequestHeader("Authorization") String authHeader,
      @Valid @RequestBody CompleteSection req,
      @PathVariable String id) {
    String token = authHeader.substring(7);
    progressOrchestratorService.completeSection(token, id, req);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
