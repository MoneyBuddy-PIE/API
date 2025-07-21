package moneybuddy.fr.moneybuddy.dtos.subAccountCourse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccountCourseComplete {

    @NotNull(message = "Le nombre de bonne réponse doit est mandatory")
    private int questionAnswered;

}