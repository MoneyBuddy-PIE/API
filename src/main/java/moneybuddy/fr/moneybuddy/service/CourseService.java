package moneybuddy.fr.moneybuddy.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.ResponseDto;
import moneybuddy.fr.moneybuddy.dtos.course.CreateCourseRequest;
import moneybuddy.fr.moneybuddy.model.ChapterWithCourses;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.repository.ChapterWithCoursesRepository;
import moneybuddy.fr.moneybuddy.repository.CourseRepository;
import moneybuddy.fr.moneybuddy.utils.Utils;

@Service
@RequiredArgsConstructor
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final ChapterWithCoursesRepository chapterWithCoursesRepository;
    private final CloudflareService cloudflareService;
    private final JwtService jwtService;
    private final Utils utils;

    public ResponseEntity<List<Course>> getCourses(String id) {
        List<Course> courses = courseRepository.findAllByChapterIdAndLockedFalse(id).orElseThrow();
        return ResponseEntity.status(200).body(courses);
    }

    public ResponseEntity<Page<Course>> getAllCourses(
        int page, 
        int size, 
        String sortBy, 
        String sortDir
    ) { 
        Pageable pageable = utils.pagination(page, size, sortBy, sortDir);
        Page<Course> courses = courseRepository.findAll(pageable);

        return ResponseEntity.status(HttpStatus.SC_OK).body(courses);
    }
 
    public ResponseEntity<Course> getCourse(String id) {
        Course course = courseRepository.findById(id).orElseThrow();
        return ResponseEntity.status(200).body(course);
    }

    public ResponseEntity<Course> createCourse(
        String token, 
        CreateCourseRequest req
    ) throws FileUploadException {
        String creator= jwtService.extractUsername(token);
        ChapterWithCourses chapter = chapterWithCoursesRepository.findById(req.getChapterId()).orElseThrow();

        String image_url = cloudflareService.uploadImage(req.getFile());
        
        Course course = Course.builder()
                        .creator(creator)
                        .chapterId(req.getChapterId())
                        .title(req.getTitle())
                        .readTime(req.getReadTime())
                        .order(req.getOrder())
                        .locked(true)
                        .image_url(image_url)
                        .resources(req.getResources())
                        .sections(req.getSections())
                        .build();

        Course saved = courseRepository.save(course);

        List<Course> courses = chapter.getCourses();

        if (courses == null) {
            courses = new ArrayList<>();
        }

        courses.add(saved);
        chapter.setCourses(courses);
        chapterWithCoursesRepository.save(chapter);

        return ResponseEntity.status(201).body(saved);
    }

    public ResponseEntity<ResponseDto> deleteCourse(String id) {
        courseRepository.deleteById(id);
        return ResponseEntity.status(204).body(null);
    }
}
