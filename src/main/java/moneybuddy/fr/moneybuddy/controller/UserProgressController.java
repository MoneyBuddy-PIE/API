/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.ResponseDto;
import moneybuddy.fr.moneybuddy.dtos.section.CompleteSection;
import moneybuddy.fr.moneybuddy.model.enums.CompletedCourse;
import moneybuddy.fr.moneybuddy.service.ProgressOrchestratorService;
import moneybuddy.fr.moneybuddy.utils.Utils;
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
  private final Utils utils;

  @PutMapping("/course/{courseId}")
  public ResponseEntity<ResponseDto> completeCourse(
      @RequestHeader("Authorization") String authHeader, @PathVariable String courseId) {
    String token = authHeader.substring(7);
    CompletedCourse status = progressOrchestratorService.completeCourse(token, courseId);
    ResponseDto res = utils.ResponseMessage(status);
    return ResponseEntity.status(res.getStatus())
        .body(
            ResponseDto.builder()
                .message(String.format("%s For course with id : %s", res.getMessage(), courseId))
                .build());
  }

  @PutMapping("/section/{sectionId}")
  public ResponseEntity<ResponseDto> completeSection(
      @RequestHeader("Authorization") String authHeader,
      @Valid @RequestBody CompleteSection req,
      @PathVariable String sectionId) {
    String token = authHeader.substring(7);
    CompletedCourse status = progressOrchestratorService.completeSection(token, sectionId, req);
    ResponseDto res = utils.ResponseMessage(status);
    return ResponseEntity.status(res.getStatus())
        .body(
            ResponseDto.builder()
                .message(String.format("%s For section with id : %s", res.getMessage(), sectionId))
                .build());
  }
}
