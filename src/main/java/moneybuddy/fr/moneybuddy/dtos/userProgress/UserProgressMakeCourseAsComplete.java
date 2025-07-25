package moneybuddy.fr.moneybuddy.dtos.userProgress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressMakeCourseAsComplete {

    @NotBlank(message = "La réponse doit avoir un intitulé")
    private String courseId;

    @PositiveOrZero
    private int subAccountScore;

    @PositiveOrZero
    private int quizScore;
}