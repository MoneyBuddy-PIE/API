package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.QuestionRequest;

@Document(collection = "courses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    private String id;

    private String title;
    private String description;
    private List<Question> questions;
    private int read_time;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
