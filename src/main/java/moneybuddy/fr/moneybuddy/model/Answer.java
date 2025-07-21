package moneybuddy.fr.moneybuddy.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "answers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Answer {
    private String answer;
    private boolean correct;
}
