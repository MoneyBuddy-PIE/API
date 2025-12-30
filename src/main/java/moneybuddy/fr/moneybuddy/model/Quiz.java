/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.util.HashMap;
import java.util.Map;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quizzes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
  @Id String id;
  private String sectionId;
  private String courseId;

  private String question;

  @Builder.Default private Map<String, String> options = new HashMap<>();

  private int correctAnswerIndex;
}
