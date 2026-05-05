/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.quiz;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.*;
import lombok.*;
import moneybuddy.fr.moneybuddy.model.Quiz.WrongResponse;
import moneybuddy.fr.moneybuddy.model.enums.QuizType;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizRequest {
  @NotBlank(message = "SectionId is mandatory")
  private String sectionId;

  @NotBlank(message = "Question is mandatory")
  private String question;

  private String response;
  private MultipartFile file;
  private List<String> moneyValues;
  private List<WrongResponse> wrongAnswers;

  private Map<String, String> options;
  private List<MultipartFile> optionsImages;

  @Min(0)
  @NotNull(message = "CorrectAnswerIndex has to have at least one correct answer")
  private Integer correctAnswerIndex;

  @NotNull(message = "Insert quiz type")
  private QuizType quizType;

  @AssertTrue(
      message = "QuizType CALCULATE has to have moneyValues length > 0 and options lenght = 1")
  public boolean isQuizTypeCorrect() {
    if (quizType == null) return true;

    if (QuizType.CALCULATE.equals(quizType))
      return moneyValues != null
          && !moneyValues.isEmpty()
          && options != null
          && options.size() == 1;

    if (QuizType.IMAGES.equals(quizType)) return optionsImages != null && optionsImages.size() >= 2;

    return options != null && options.size() >= 2;
  }
}
