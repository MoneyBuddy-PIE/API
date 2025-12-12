/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.userProgress;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressMakeCourseAsComplete {

  @NotBlank(message = "La réponse doit avoir un intitulé")
  private String courseId;

  @PositiveOrZero private int subAccountScore;

  @PositiveOrZero private int quizScore;
}
