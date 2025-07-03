package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AnswerRequest;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.CourseRequest;
import moneybuddy.fr.moneybuddy.dtos.QuestionRequest;
import moneybuddy.fr.moneybuddy.model.Answer;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.Question;
import moneybuddy.fr.moneybuddy.model.Role;
import moneybuddy.fr.moneybuddy.model.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.repository.AccountRepository;
import moneybuddy.fr.moneybuddy.repository.CourseRepository;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final JwtService jwtService;

    public ResponseEntity<AuthResponse> response(String message, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(AuthResponse.builder()
                    .error(message)
                    .build());
    }

    // public List<Course> dataResponse(String message, HttpStatus status) {
    //     return courseRepository.findAll();
    // }


    public ResponseEntity<AuthResponse> createCourse (CourseRequest request, String token) {        
        Role role = jwtService.extractAccountRole(token);

        if (!Role.ADMIN.equals(role)) {
            return response("Vous n'avez pas les droits", HttpStatus.FORBIDDEN);
        }

        Course course = Course.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .questions(request.getQuestions())
                    .read_time(0)
                    .createdAt(LocalDateTime.now())
                    .build();
                    
        courseRepository.save(course);
        return response("Course created", HttpStatus.CREATED);
    }

    // public ResponseEntity<List<Course>> getCourses (String token, String source) {
    //     List<Course> courses = new ArrayList<>();
    //     SubAccountRole role = jwtService.extractSubAccountRole(token);
    //     // String id = (source != null && source.isEmpty()) ? 
    //     //     jwtService.extractSubAccountAccountId(token) 
    //     //     : jwtService.extractSubAccountId(token);
        
    //     if ("PARENT".equals(source) && SubAccountRole.PARENT.equals(role)) {
    //         courses = courseRepository.findAll();
    //     } else if (SubAccountRole.PARENT.equals(role)) {
    //         courses = courseRepository.findAll();
    //     } else {
    //         return dataResponse("Vous n'avez pas les droits", HttpStatus.FORBIDDEN);
    //     }

    //     return ResponseEntity.status(HttpStatus.ACCEPTED).body(courses);
    // }

    // public ResponseEntity<Course> getTask (String id) {
    //     Optional<Course> task = taskRepository.findById(id);

    //     return task.isPresent() ? 
    //         ResponseEntity.status(HttpStatus.ACCEPTED).body(task.get())
    //         : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    // }

    // public ResponseEntity<AuthResponse> deleteCourse (String token, String courseId) {
    //     // String subAccountId = jwtService.extractSubAccountId(token);
    //     Optional<Course> course = courseRepository.findById(courseId);

    //     if (course.isPresent()) {
    //         courseRepository.deleteById(courseId);
    //         return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    //     }

    //     return response("Erreur lors de la suppression", HttpStatus.BAD_REQUEST);
    // }

    // public ResponseEntity<AuthResponse> completeReadTimeAmount (String token, String courseId) {
    //     SubAccountRole role = jwtService.extractSubAccountRole(token);
        
    //     if (!SubAccountRole.PARENT.equals(role)) {
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    //     }

    //     Optional<Course> optionalCourse = courseRepository.findById(courseId);
        
    //     if (optionalCourse.isPresent()) {
    //         Course course = optionalCourse.get();
    //         course.setRead_time(course.getRead_time() + 1);
    //         course.setUpdatedAt(LocalDateTime.now());
    //         courseRepository.save(course);
    //         return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    //     }

    //     return response("Erreur lors de l'update", HttpStatus.BAD_REQUEST);
    // }

    // public ResponseEntity<Course> modifyCourse (CourseRequest request, String token, String courseId) {
    //     SubAccountRole role = jwtService.extractSubAccountRole(token);

    //     if (!SubAccountRole.PARENT.equals(role)) {
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    //     }

    //     Optional<Course> optionalCourse = courseRepository.findById(courseId);
    //     if (optionalCourse.isPresent()) {
    //         Course course = optionalCourse.get();

    //         course.setTitle(request.getTitle());
    //         course.setDescription(request.getDescription());
    //         course.setQuestions(request.getQuestions());
    //         course.setUpdatedAt(LocalDateTime.now());

    //         Course updatedCourse = courseRepository.save(course);
    //         return ResponseEntity.status(HttpStatus.OK).body(updatedCourse);
    //     }
        
    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    // }
}
