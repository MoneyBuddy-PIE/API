/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.quiz.CreateQuizRequest;
import moneybuddy.fr.moneybuddy.dtos.quiz.UpdateQuizRequest;
import moneybuddy.fr.moneybuddy.exception.QuizNotFoundException;
import moneybuddy.fr.moneybuddy.exception.SectionNotFoundException;
import moneybuddy.fr.moneybuddy.model.Quiz;
import moneybuddy.fr.moneybuddy.model.Section;
import moneybuddy.fr.moneybuddy.model.enums.QuizType;
import moneybuddy.fr.moneybuddy.repository.QuizRepository;
import moneybuddy.fr.moneybuddy.repository.SectionRepository;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {
  private final QuizRepository quizRepository;
  private final SectionRepository sectionRepository;
  private final CloudflareService cloudflareService;

  public Quiz getById(String id) {
    return quizRepository.findById(id).orElseThrow(() -> new QuizNotFoundException(id));
  }

  public Section getSection(String sectionId) {
    return sectionRepository
        .findById(sectionId)
        .orElseThrow(() -> new SectionNotFoundException(sectionId));
  }

  public Quiz createQuiz(CreateQuizRequest req)
      throws FileUploadException, JsonMappingException, JsonProcessingException {
    Section section = getSection(req.getSectionId());

    Map<String, String> resolvedOptions;
    if (QuizType.IMAGES.equals(req.getQuizType())) {
      resolvedOptions = new LinkedHashMap<>();
      List<org.springframework.web.multipart.MultipartFile> images = req.getOptionsImages();
      for (int i = 0; i < images.size(); i++) {
        String key = cloudflareService.uploadImage(images.get(i));
        resolvedOptions.put("option" + (i + 1), key);
      }
    } else {
      resolvedOptions = req.getOptions();
    }

    Quiz quiz =
        Quiz.builder()
            .correctAnswerIndex(req.getCorrectAnswerIndex())
            .options(resolvedOptions)
            .question(req.getQuestion())
            .response(req.getResponse())
            .sectionId(req.getSectionId())
            .courseId(section.getCourseId())
            .quizType(req.getQuizType())
            .wrongAnswers(req.getWrongAnswers())
            .moneyValues(req.getMoneyValues())
            .build();

    if (req.getFile() != null && req.getFile().getSize() > 0) {
      String imageUrl = cloudflareService.uploadImage(req.getFile());
      quiz.setImageUrl(imageUrl);
    }

    quiz = quizRepository.save(quiz);

    section.getQuiz().put(quiz.getId(), quiz);
    sectionRepository.save(section);

    return quiz;
  }

  public Quiz updateQuiz(String quizId, UpdateQuizRequest req)
      throws FileUploadException, JsonMappingException, JsonProcessingException {
    Quiz quiz = getById(quizId);

    if (req.getCorrectAnswerIndex() >= 0) quiz.setCorrectAnswerIndex(req.getCorrectAnswerIndex());

    quiz.setQuestion(Optional.ofNullable(req.getQuestion()).orElse(quiz.getQuestion()));
    quiz.setResponse(Optional.ofNullable(req.getResponse()).orElse(quiz.getResponse()));
    quiz.setQuizType(Optional.ofNullable(req.getQuizType()).orElse(quiz.getQuizType()));
    quiz.setWrongAnswers(Optional.ofNullable(req.getWrongAnswers()).orElse(quiz.getWrongAnswers()));
    quiz.setMoneyValues(Optional.ofNullable(req.getMoneyValues()).orElse(quiz.getMoneyValues()));

    if (req.getFile() != null && req.getFile().getSize() > 0) {
      String image_url = cloudflareService.uploadImage(req.getFile());
      cloudflareService.remove(quiz.getImageUrl());
      quiz.setImageUrl(image_url);
    }

    if (QuizType.IMAGES.equals(quiz.getQuizType())
        && req.getOptionsImages() != null
        && !req.getOptionsImages().isEmpty()) {
      // Delete old R2 images before uploading new ones
      if (quiz.getOptions() != null) {
        quiz.getOptions().values().forEach(cloudflareService::remove);
      }
      Map<String, String> newOptions = new LinkedHashMap<>();
      List<org.springframework.web.multipart.MultipartFile> images = req.getOptionsImages();
      for (int i = 0; i < images.size(); i++) {
        String key = cloudflareService.uploadImage(images.get(i));
        newOptions.put("option" + (i + 1), key);
      }
      quiz.setOptions(newOptions);
    } else if (req.getOptions() != null && !req.getOptions().isEmpty()) {
      Map<String, String> options = new LinkedHashMap<>();
      for (int i = 0; i < req.getOptions().size(); i++) {
        options.put("option" + (i + 1), req.getOptions().get(i));
      }
      quiz.setOptions(options);
    }

    return quizRepository.save(quiz);
  }

  public void deleteQuiz(String quizId) {
    Quiz quiz = getById(quizId);
    Section section = getSection(quiz.getSectionId());

    section.getQuiz().remove(quizId);
    sectionRepository.save(section);

    quizRepository.delete(quiz);
  }
}
