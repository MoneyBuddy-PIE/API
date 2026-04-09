/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.quiz;

import java.util.List;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateQuizRequest {
  private String question;
  private String response;

  private List<String> options;

  @Min(0)
  private int correctAnswerIndex;
}
