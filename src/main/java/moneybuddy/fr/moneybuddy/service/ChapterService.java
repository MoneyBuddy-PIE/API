package moneybuddy.fr.moneybuddy.service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.ResponseDto;
import moneybuddy.fr.moneybuddy.dtos.chapter.CreateChapterRequest;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.ChapterWithCourses;
import moneybuddy.fr.moneybuddy.model.ChapterWithoutCourses;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.repository.ChapterRepository;
import moneybuddy.fr.moneybuddy.repository.ChapterWithoutCoursesRepository;
import moneybuddy.fr.moneybuddy.repository.CourseRepository;
import moneybuddy.fr.moneybuddy.utils.Utils;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterWithoutCoursesRepository chapterWithoutCoursesRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;

    private final CloudflareService cloudflareService;
    private final JwtService jwtService;
    private final Utils utils;

    public ResponseEntity<Page<ChapterWithoutCourses>> getChapters(
        String token,
        int page, 
        int size, 
        String sortBy, 
        String sortDir
    ) {
        SubAccountRole subAccountRole = jwtService.extractSubAccountRole(token);
        subAccountRole = SubAccountRole.OWNER.equals(subAccountRole) ? SubAccountRole.PARENT : subAccountRole;
        
        Pageable pageable = utils.pagination(page, size, sortBy, sortDir);
        Page<ChapterWithoutCourses> chapters = chapterWithoutCoursesRepository.findAllBySubAccountRoleAndLockedFalse(subAccountRole, pageable);

        return ResponseEntity.status(200).body(chapters);
    }

    public ResponseEntity<Page<ChapterWithoutCourses>> getAllChapters(
        int page, 
        int size, 
        String sortBy, 
        String sortDir
    ) { 
        Pageable pageable = utils.pagination(page, size, sortBy, sortDir);
        Page<ChapterWithoutCourses> chapters = chapterWithoutCoursesRepository.findAll(pageable);

        return ResponseEntity.status(200).body(chapters);
    }    

    public ResponseEntity<ChapterWithCourses> getChapter(String id) {
        List<Course> courses = courseRepository.findAllByChapterId(id).orElseThrow();
        Chapter chapter = chapterRepository.findById(id).orElseThrow();

        ChapterWithCourses completeChapter = new ChapterWithCourses();
        BeanUtils.copyProperties(chapter, completeChapter);
        completeChapter.setCourses(courses);
        
        return ResponseEntity.status(200).body(completeChapter);
    }
         
    public ResponseEntity<Chapter> createChapter(String token, CreateChapterRequest req) throws FileUploadException {
        String creator = jwtService.extractUsername(token);
        String image_url = cloudflareService.uploadImage(req.getFile());

        Chapter chapter = Chapter.builder()
                        .title(req.getTitle())
                        .description(req.getDescription())
                        .level(req.getLevel())
                        .order(req.getOrder())
                        .image_url(image_url)
                        .locked(true)
                        .subAccountRole(req.getSubAccountRole())
                        .creator(creator)
                        .createdAt(LocalDateTime.now())
                        .build();
                    
     
        Chapter saved = chapterRepository.save(chapter);
        return ResponseEntity.status(201).body(saved);
    }

    public ResponseEntity<ResponseDto> deleteChapter (String id) {
        chapterRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.SC_OK).body(null);
    }
}