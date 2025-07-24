package moneybuddy.fr.moneybuddy.service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.chapter.CreateChapterRequest;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.Quiz;
import moneybuddy.fr.moneybuddy.model.Resource;
import moneybuddy.fr.moneybuddy.model.Section;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.repository.ChapterRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final JwtService jwtService;

    public ResponseEntity<Page<Chapter>> getChapters(
        int page, 
        int size, 
        String sortBy, 
        String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Chapter> chapters = chapterRepository.findAll(pageable);

        return ResponseEntity.status(200).body(chapters);
    }

    public ResponseEntity<Chapter> getChapter(String token, String chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();

        return ResponseEntity.status(200).body(chapter);
    }

    public ResponseEntity<Chapter> createChapter(String token, CreateChapterRequest req) {

        if (chapterRepository.findByTitle(req.getTitle()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        String accountId = jwtService.extractSubAccountAccountId(token);

        Chapter chapter = Chapter.builder()
            .title(req.getTitle())
            .description(req.getDescription())
            .order(req.getOrder())
            .createdAt(LocalDateTime.now())
            .subAccountRole(SubAccountRole.PARENT)
            .creatorId(accountId)
            .build();


        List<Course> courses = req.getCourses().stream()
            .map(courseDto -> {
                Course course = new Course();
                course.setTitle(courseDto.getTitle());
                course.setDescription(courseDto.getDescription());
                course.setOrder(courseDto.getOrder());
                course.setLocked(true);

                List<Section> sections = courseDto.getSections().stream()
                        .map(sectionDto -> {
                            Section section = new Section();
                            section.setTitle(sectionDto.getTitle());
                            section.setContent(sectionDto.getContent());

                            // Conversion du quiz si présent
                            if (sectionDto.getQuiz() != null) {
                                Quiz quiz = new Quiz();
                                quiz.setQuestion(sectionDto.getQuiz().getQuestion());
                                quiz.setOptions(sectionDto.getQuiz().getOptions());
                                quiz.setCorrectAnswerIndex(sectionDto.getQuiz().getCorrectAnswerIndex());
                                quiz.setMinimumScoreToPass(sectionDto.getQuiz().getMinimumScoreToPass());
                                section.setQuiz(quiz);
                            }

                            return section;
                        })
                        .collect(Collectors.toList());

                course.setSections(sections);

                if (courseDto.getResources() != null) {
                    List<Resource> resources = courseDto.getResources().stream()
                            .map(resourceDto -> {
                                Resource resource = new Resource();
                                resource.setTitle(resourceDto.getTitle());
                                resource.setType(resourceDto.getType());
                                resource.setUrl(resourceDto.getUrl());

                                return resource;

                            })
                            .collect(Collectors.toList());
                    course.setResources(resources);
                }

                return course;
            })
            .collect(Collectors.toList());

        chapter.setCourses(courses);

        Chapter savedChapter = chapterRepository.save(chapter);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedChapter);
    }
}