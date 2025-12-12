/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.util.List;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
  private String question;

  private List<String> options;

  private int correctAnswerIndex;

  @Builder.Default private int minimumScoreToPass = 70;
}
