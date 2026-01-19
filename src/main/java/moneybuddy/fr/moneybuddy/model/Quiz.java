/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonIgnore @Builder.Default private Map<String, String> options = new HashMap<>();

  @JsonProperty("options")
  public List<String> getOptionsAsList() {
    return new ArrayList<>(options.values());
  }

  private int correctAnswerIndex;
}
