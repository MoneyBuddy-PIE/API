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
import moneybuddy.fr.moneybuddy.model.enums.QuizType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quizzes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
  @Id private String id;
  private String sectionId;
  private String courseId;

  private String question;
  private String imageUrl;
  private String response;
  private QuizType quizType;

  @JsonIgnore @Builder.Default private Map<String, String> options = new HashMap<>();
  private int correctAnswerIndex;
  private List<WrongResponse> wrongAnswers;

  private List<String> moneyValues;

  @JsonProperty("options")
  public List<String> getOptionsAsList() {
    return new ArrayList<>(options.values());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class WrongResponse {
    private int answerIndex;
    private String response;
  }
}
