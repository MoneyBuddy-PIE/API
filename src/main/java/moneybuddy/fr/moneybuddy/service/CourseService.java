package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.CourseRequest;
import moneybuddy.fr.moneybuddy.dtos.subAccountCourse.SubAccountCourseComplete;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.enums.Role;
import moneybuddy.fr.moneybuddy.model.SubAccountCourse;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.repository.AccountRepository;
import moneybuddy.fr.moneybuddy.repository.CourseRepository;
import moneybuddy.fr.moneybuddy.repository.SubAccountCourseRepository;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final AccountRepository accountRepository;
    private final SubAccountCourseRepository subAccountCourseRepository;
    private final JwtService jwtService;

    public ResponseEntity<AuthResponse> response(String message, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(AuthResponse.builder()
                    .error(message)
                    .build());
    }

    public Boolean isAdmin(String token) {
        String email = jwtService.extractSubAccountEmail(token) != null ? 
                    jwtService.extractSubAccountEmail(token) : 
                    jwtService.extractUsername(token);

        Account account = accountRepository.findByEmail(email).orElseThrow();
        return Role.ADMIN.equals(account.getRole());
    }

    public ResponseEntity<AuthResponse> createCourse (CourseRequest request, String token) {        
        if (!isAdmin(token)) {
            return response("Not allowed", HttpStatus.FORBIDDEN);
        }

        Course course = Course.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .subAccountRole(request.getSubAccountRole())
                    .questions(request.getQuestions())
                    .readTime("5")
                    .createdAt(LocalDateTime.now())
                    .build();
                    
        courseRepository.save(course);
        return response("Course created", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Course>> getCourses(String token) {
        SubAccountRole role = jwtService.extractSubAccountRole(token);

        List<Course> courses = courseRepository.findAllBySubAccountRole(SubAccountRole.OWNER.equals(role) ? SubAccountRole.PARENT : role);
        return ResponseEntity.status(HttpStatus.OK).body(courses);
    }

    public ResponseEntity<Course> getCourse(String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();

        return ResponseEntity.status(HttpStatus.OK).body(course);
    }

    public ResponseEntity<AuthResponse> deleteCourse (String token, String courseId) {
        if (!isAdmin(token)) {
            return response("Not allowed", HttpStatus.FORBIDDEN);
        }

        courseRepository.deleteById(courseId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public ResponseEntity<AuthResponse> completeCourse (SubAccountCourseComplete req, String token, String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        String subAccountId = jwtService.extractSubAccountId(token);
        
        int questions = course.getQuestions().size();

        SubAccountCourse subAccountCourse = SubAccountCourse.builder()
        .subAccountid(subAccountId)
        .courseId(courseId)
        .createdAt(LocalDateTime.now())
        .score(String.valueOf((req.getQuestionAnswered()*100)/questions))
        .isCompleted(questions == req.getQuestionAnswered())
        .build();
        subAccountCourseRepository.save(subAccountCourse);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    public ResponseEntity<AuthResponse> modifyCourse (CourseRequest request, String token, String courseId) {
        if (!isAdmin(token)) {
            return response("Not allowed", HttpStatus.FORBIDDEN);
        }

        Course course = courseRepository.findById(courseId).orElseThrow();

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setReadTime(request.getRead_time());
        course.setSubAccountRole(request.getSubAccountRole());
        course.setUpdatedAt(LocalDateTime.now());
        course.setQuestions(request.getQuestions());

        courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
