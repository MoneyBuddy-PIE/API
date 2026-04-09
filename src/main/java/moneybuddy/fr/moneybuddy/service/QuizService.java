/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.quiz.CreateQuizRequest;
import moneybuddy.fr.moneybuddy.dtos.quiz.UpdateQuizRequest;
import moneybuddy.fr.moneybuddy.exception.QuizNotFoundException;
import moneybuddy.fr.moneybuddy.exception.SectionNotFoundException;
import moneybuddy.fr.moneybuddy.model.Quiz;
import moneybuddy.fr.moneybuddy.model.Section;
import moneybuddy.fr.moneybuddy.repository.QuizRepository;
import moneybuddy.fr.moneybuddy.repository.SectionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {
  private final QuizRepository quizRepository;
  private final SectionRepository sectionRepository;

  public Quiz getById(String id) {
    return quizRepository.findById(id).orElseThrow(() -> new QuizNotFoundException(id));
  }

  public Section getSection(String sectionId) {
    return sectionRepository
        .findById(sectionId)
        .orElseThrow(() -> new SectionNotFoundException(sectionId));
  }

  public Quiz createQuiz(CreateQuizRequest req) {
    Section section = getSection(req.getSectionId());

    Quiz quiz =
        Quiz.builder()
            .correctAnswerIndex(req.getCorrectAnswerIndex())
            .options(req.getOptions())
            .question(req.getQuestion())
            .response(req.getResponse())
            .sectionId(req.getSectionId())
            .courseId(section.getCourseId())
            .build();
    quiz = quizRepository.save(quiz);

    section.getQuiz().put(quiz.getId(), quiz);
    sectionRepository.save(section);

    return quiz;
  }

  public Quiz updateQuiz(String quizId, UpdateQuizRequest req) {
    Quiz quiz = getById(quizId);

    if (req.getCorrectAnswerIndex() >= 0) quiz.setCorrectAnswerIndex(req.getCorrectAnswerIndex());
    quiz.setQuestion(Optional.ofNullable(req.getQuestion()).orElse(quiz.getQuestion()));
    quiz.setResponse(Optional.ofNullable(req.getResponse()).orElse(quiz.getResponse()));
    if (req.getOptions() != null && !req.getOptions().isEmpty()) {
      Map<String, String> options = new HashMap<>();
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
