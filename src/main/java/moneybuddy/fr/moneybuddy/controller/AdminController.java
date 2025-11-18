package moneybuddy.fr.moneybuddy.controller;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.ResponseDto;
import moneybuddy.fr.moneybuddy.dtos.chapter.CreateChapterRequest;
import moneybuddy.fr.moneybuddy.dtos.course.CreateCourseRequest;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.ChapterWithCourses;
import moneybuddy.fr.moneybuddy.model.ChapterWithoutCourses;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.model.enums.PlanType;
import moneybuddy.fr.moneybuddy.service.AccountService;
import moneybuddy.fr.moneybuddy.service.ChapterService;
import moneybuddy.fr.moneybuddy.service.CourseService;
import moneybuddy.fr.moneybuddy.service.TransactionService;
import moneybuddy.fr.moneybuddy.model.DepositDetails;
import moneybuddy.fr.moneybuddy.service.GoalTransactionService;

import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
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
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final GoalTransactionService goalTransactionService;

    //Transactions
    @GetMapping("/transactions")
    public ResponseEntity<Page<Transaction>> getAllTransaction (
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "order") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return transactionService.getAllTransactions(page, size, sortBy, sortDir);
    }
 
    //Chapters
    @PostMapping("/chapters")
    public ResponseEntity<Chapter> createChapter(
        @Valid @ModelAttribute CreateChapterRequest request,
        @RequestHeader("Authorization") String authHeader
    ) throws FileUploadException {
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

    @GetMapping("/chapters/{id}")
    public ResponseEntity<ChapterWithCourses> getChapter (
        @PathVariable String id
    ) {
        return chapterService.getChapter(id);
    }

    @DeleteMapping("/chapters/{id}")
    public ResponseEntity<ResponseDto> deleteChapter (
        @PathVariable String id
    ) {
        return chapterService.deleteChapter(id);
    }

    //Courses
    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(
        @Valid @RequestBody CreateCourseRequest request,
        @RequestHeader("Authorization") String authHeader
    ) throws FileUploadException {
        String token = authHeader.substring(7);
        return courseService.createCourse(token, request);
    }

    @DeleteMapping("/courses")
    public ResponseEntity<ResponseDto> deleteCourse(
        @PathVariable String id
    ) {
        return courseService.deleteCourse(id);
    }

    @GetMapping("/courses")
    public ResponseEntity<Page<Course>> getAllCourses(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "order") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return courseService.getAllCourses(page, size, sortBy, sortDir);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourse (
        @PathVariable String id
    ) {
        return courseService.getCourse(id);
    }

    //Users
    @GetMapping("/accounts")
    public ResponseEntity<Page<Account>> getAccounts (
        @RequestParam(required = false) PlanType planType,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "order") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return accountService.getAccounts(planType, page, size, sortBy, sortDir);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getAccount (
        @PathVariable String id
    ) {
        return accountService.getAccount(id);
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<ResponseDto> deleteAccount (
        @PathVariable String id
    ) {
        return accountService.deleteAccount(id);
    }

    @DeleteMapping("/accounts/{id}/desable")
    public ResponseEntity<ResponseDto> desableAccount (
        @PathVariable String id
    ) {
        return accountService.desableAccount(id);
    }

    @GetMapping("/accounts/{id}/transactions")
    public ResponseEntity<Page<Transaction>> getAccountTransaction(
        @PathVariable String id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "order") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return transactionService.getAllTransactionsByAccountId(id, page, size, sortBy, sortDir);
    }

    //Goals Transactions
    @GetMapping("/goals/transactions")
    public ResponseEntity<Page<DepositDetails>> getAllGoalTransactions (
        @RequestParam(required = false) String accountId,
        @RequestParam(required = false) String type,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "order") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        if(type.equals("account") && accountId != null) {
            return goalTransactionService.getAllGoalTransactionsByAccountId(accountId, page, size, sortBy, sortDir);
        }

        if(type.equals( "parent") && accountId != null) {
            return goalTransactionService.getAllGoalTransactionsByParentId(accountId, page, size, sortBy, sortDir);
        }

        if(type.equals("child") && accountId != null) {
            return goalTransactionService.getAllGoalTransactionsByChildId(accountId, page, size, sortBy, sortDir);
        }

        return goalTransactionService.getAllGoalTransactions(page, size, sortBy, sortDir);
    }

    @GetMapping("/goal/{goalId}/transactions")
    public ResponseEntity<List<DepositDetails>> getSingleGoalTransactions(
        @PathVariable String goalId
    ) {
        return goalTransactionService.getSingleGoalTransactions(goalId);
    }

}