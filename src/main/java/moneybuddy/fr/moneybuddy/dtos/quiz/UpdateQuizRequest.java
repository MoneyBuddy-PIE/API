/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.quiz;

import java.util.List;

import jakarta.validation.constraints.Min;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.Quiz.WrongResponse;
import moneybuddy.fr.moneybuddy.model.enums.QuizType;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateQuizRequest {
  private String question;
  private String response;

  private MultipartFile file;
  private List<String> moneyValues;
  private List<WrongResponse> wrongAnswers;
  private QuizType quizType;

  private List<String> options;
  private List<MultipartFile> optionsImages;

  @Min(0)
  private int correctAnswerIndex;
}
