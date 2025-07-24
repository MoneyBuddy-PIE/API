package moneybuddy.fr.moneybuddy.dtos.quiz;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {
    @NotBlank(message = "Question is mandatory")
    private String question;
    
    @NotEmpty(message = "At least two options are required")
    private List<@NotBlank String> options;
    
    @Min(0)
    private int correctAnswerIndex;
    
    @Min(50) @Max(100)
    @Builder.Default
    private int minimumScoreToPass = 70;
}
