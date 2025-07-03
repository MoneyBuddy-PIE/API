package moneybuddy.fr.moneybuddy.model;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
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
    
    @Id
    private String id;

    private String answer;
    private boolean isTrue;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
