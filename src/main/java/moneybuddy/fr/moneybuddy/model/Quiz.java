package moneybuddy.fr.moneybuddy.model;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    private String question;
    
    private List<String> options;
    
    private int correctAnswerIndex;
    
    @Builder.Default
    private int minimumScoreToPass = 70;
}