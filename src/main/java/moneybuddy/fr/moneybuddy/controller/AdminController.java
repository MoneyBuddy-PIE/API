/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.ResponseDto;
import moneybuddy.fr.moneybuddy.dtos.chapter.ChapterWithoutCoursesForAdmin;
import moneybuddy.fr.moneybuddy.dtos.chapter.CreateChapterRequest;
import moneybuddy.fr.moneybuddy.dtos.chapter.UpdateChapterRequest;
import moneybuddy.fr.moneybuddy.dtos.course.CreateCourseRequest;
import moneybuddy.fr.moneybuddy.dtos.course.UpdateCourseRequest;
import moneybuddy.fr.moneybuddy.dtos.quiz.CreateQuizRequest;
import moneybuddy.fr.moneybuddy.dtos.quiz.UpdateQuizRequest;
import moneybuddy.fr.moneybuddy.dtos.ressource.CreateRessourceRequest;
import moneybuddy.fr.moneybuddy.dtos.ressource.UpdateRessourceRequest;
import moneybuddy.fr.moneybuddy.dtos.section.CreateSectionRequest;
import moneybuddy.fr.moneybuddy.dtos.section.UpdateSectionRequest;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.Device;
import moneybuddy.fr.moneybuddy.model.Quiz;
import moneybuddy.fr.moneybuddy.model.Ressource;
import moneybuddy.fr.moneybuddy.model.Section;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.model.enums.PlanType;
import moneybuddy.fr.moneybuddy.service.AccountService;
import moneybuddy.fr.moneybuddy.service.ChapterService;
import moneybuddy.fr.moneybuddy.service.CourseService;
import moneybuddy.fr.moneybuddy.service.DeviceService;
import moneybuddy.fr.moneybuddy.service.QuizService;
import moneybuddy.fr.moneybuddy.service.RessourceService;
import moneybuddy.fr.moneybuddy.service.SectionService;
import moneybuddy.fr.moneybuddy.service.SubAccountService;
import moneybuddy.fr.moneybuddy.service.TaskService;
import moneybuddy.fr.moneybuddy.service.TransactionService;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final ChapterService chapterService;
  private final CourseService courseService;
  private final RessourceService ressourceService;
  private final SectionService sectionService;
  private final QuizService quizService;
  private final AccountService accountService;
  private final SubAccountService subAccountService;
  private final TransactionService transactionService;
  private final TaskService taskService;
  private final DeviceService deviceService;

  // Transactions
  @GetMapping("/transactions")
  public ResponseEntity<Page<Transaction>> getAllTransaction(
      @RequestParam(required = false) String accountId,
      @RequestParam(required = false) String subAccountId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "order") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            transactionService.getAllTransactions(
                page, size, sortBy, sortDir, accountId, subAccountId));
  }

  // Chapters
  @PostMapping("/chapters")
  public ResponseEntity<Chapter> createChapter(
      @Valid @ModelAttribute CreateChapterRequest request,
      @RequestHeader("Authorization") String authHeader)
      throws FileUploadException {
    String token = authHeader.substring(7);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(chapterService.createChapter(token, request));
  }

  @GetMapping("/chapters")
  public ResponseEntity<Page<ChapterWithoutCoursesForAdmin>> getAllChapters(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(chapterService.getAllChapters(page, size, sortBy, sortDir));
  }

  @GetMapping("/chapters/{id}")
  public ResponseEntity<Chapter> getChapter(@PathVariable String id) {
    return ResponseEntity.status(HttpStatus.OK).body(chapterService.getTotalChapter(id));
  }

  @PutMapping("/chapters/{id}")
  public ResponseEntity<Chapter> updateChapter(
      @Valid @ModelAttribute UpdateChapterRequest request, @PathVariable String id)
      throws FileUploadException, JsonMappingException, JsonProcessingException {
    return ResponseEntity.status(HttpStatus.OK).body(chapterService.updateCourse(id, request));
  }

  @DeleteMapping("/chapters/{id}")
  public ResponseEntity<ResponseDto> deleteChapter(@PathVariable String id) {
    chapterService.deleteChapter(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDto.builder().message("Chapter deleted").build());
  }

  // Courses
  @PostMapping("/courses")
  public ResponseEntity<Course> createCourse(
      @Valid @ModelAttribute CreateCourseRequest request,
      @RequestHeader("Authorization") String authHeader)
      throws FileUploadException, JsonMappingException, JsonProcessingException {
    String token = authHeader.substring(7);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(courseService.createCourse(token, request));
  }

  @DeleteMapping("/courses/{courseId}")
  public ResponseEntity<ResponseDto> deleteCourse(@PathVariable String courseId) {
    courseService.deleteCourse(courseId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDto.builder().message("Course deleted").build());
  }

  @GetMapping("/courses")
  public ResponseEntity<Page<Course>> getAllCourses(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "order") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(courseService.getAllCourses(page, size, sortBy, sortDir));
  }

  @GetMapping("/courses/{id}")
  public ResponseEntity<Course> getCourse(@PathVariable String id) {
    return ResponseEntity.status(HttpStatus.FOUND).body(courseService.getById(id));
  }

  @PutMapping("/courses/{id}")
  public ResponseEntity<Course> updateCourse(
      @Valid @ModelAttribute UpdateCourseRequest request, @PathVariable String id)
      throws FileUploadException, JsonMappingException, JsonProcessingException {
    return ResponseEntity.status(HttpStatus.OK).body(courseService.updateCourse(id, request));
  }

  @DeleteMapping("/courses/ressources/{ressourceId}")
  public ResponseEntity<ResponseDto> deleteRessourceFromCourse(@PathVariable String ressourceId) {
    ressourceService.deleteRessource(ressourceId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDto.builder().message("Ressource deleted").build());
  }

  @DeleteMapping("/courses/sections/{sectionId}")
  public ResponseEntity<ResponseDto> deleteSectionFromCourse(@PathVariable String sectionId) {
    sectionService.deleteSection(sectionId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDto.builder().message("Section deleted").build());
  }

  // Ressources
  @PostMapping("/courses/ressources")
  public ResponseEntity<Ressource> createRessource(@Valid @RequestBody CreateRessourceRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ressourceService.createRessource(req));
  }

  @PutMapping("/courses/ressources/{ressourceId}")
  public ResponseEntity<Ressource> updateRessource(
      @Valid @RequestBody UpdateRessourceRequest req, @PathVariable String ressourceId) {
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(ressourceService.updateRessource(ressourceId, req));
  }

  // Sections
  @PostMapping("/courses/sections")
  public ResponseEntity<Section> createSection(@Valid @RequestBody CreateSectionRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(sectionService.createSection(req));
  }

  @PutMapping("/courses/sections/{sectionId}")
  public ResponseEntity<Section> updateSection(
      @Valid @RequestBody UpdateSectionRequest req, @PathVariable String sectionId) {
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(sectionService.updateSection(sectionId, req));
  }

  @DeleteMapping("/courses/sections/quizzes/{quizId}")
  public ResponseEntity<ResponseDto> deleteQuizFromSection(@PathVariable String quizId) {
    quizService.deleteQuiz(quizId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDto.builder().message("Quiz deleted").build());
  }

  // Quizzes
  @PostMapping("/courses/sections/quizzes")
  public ResponseEntity<Quiz> createQuiz(@Valid @RequestBody CreateQuizRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(quizService.createQuiz(req));
  }

  @PutMapping("/courses/sections/quizzes/{quizId}")
  public ResponseEntity<Quiz> updateQuiz(
      @Valid @RequestBody UpdateQuizRequest req, @PathVariable String quizId) {
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(quizService.updateQuiz(quizId, req));
  }

  // Users
  @GetMapping("/accounts")
  public ResponseEntity<Page<Account>> getAccounts(
      @RequestParam(required = false) PlanType planType,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "order") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(accountService.getAccounts(planType, page, size, sortBy, sortDir));
  }

  @GetMapping("/accounts/{id}")
  public ResponseEntity<Account> getAccount(@PathVariable String id) {
    return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccount(id));
  }

  @DeleteMapping("/accounts/{id}")
  public ResponseEntity<ResponseDto> deleteAccount(@PathVariable String id) {
    accountService.deleteAccount(id);
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(ResponseDto.builder().message("Account deleted").build());
  }

  @DeleteMapping("/accounts/{id}/desable")
  public ResponseEntity<ResponseDto> desableAccount(@PathVariable String id) {
    accountService.desableAccount(id);
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(ResponseDto.builder().message("Account disabled").build());
  }

  @GetMapping("/accounts/{id}/transactions")
  public ResponseEntity<Page<Transaction>> getAccountTransaction(
      @PathVariable String id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "order") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(transactionService.getAllTransactionsByAccountId(id, page, size, sortBy, sortDir));
  }

  // SubAccounts

  @GetMapping("/accounts/subAccounts/{id}")
  public ResponseEntity<SubAccount> getSubAccount(@PathVariable String id) {
    return ResponseEntity.status(HttpStatus.OK).body(subAccountService.getById(id));
  }

  @GetMapping("/accounts/subAccounts/{id}/device")
  public ResponseEntity<Device> getSubAccountDevice(@PathVariable String id) {
    return ResponseEntity.status(HttpStatus.OK).body(deviceService.getDeviceBySubAccountId(id));
  }

  @GetMapping("/accounts/subAccounts/{id}/transactions")
  public ResponseEntity<Page<Transaction>> getSubAccountTransactions(
      @PathVariable String id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "order") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(transactionService.getAllTransactionsBySubAccountId(id, page, size, sortBy, sortDir));
  }

  @GetMapping("/accounts/subAccounts/{id}/tasks")
  public ResponseEntity<Page<Task>> getSubAccountTasks(
      @PathVariable String id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "order") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(taskService.getTasksBySubAccountId(id, page, size, sortBy, sortDir));
  }

  // Goals Transactions
  @GetMapping("/goals/transactions")
  public ResponseEntity<Page<Transaction>> getAllGoalTransactions(
      @RequestParam(required = false) String accountId,
      @RequestParam(required = false) String subAccountId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "order") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            transactionService.getAllTransactions(
                page, size, sortBy, sortDir, accountId, subAccountId));
  }

  @GetMapping("/goal/{goalId}/transactions")
  public ResponseEntity<List<Transaction>> getSingleGoalTransactions(@PathVariable String goalId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(transactionService.getTransactionByGoalId(goalId));
  }
}
