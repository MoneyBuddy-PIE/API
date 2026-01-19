/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Section {

  @Id private String id;
  private String courseId;
  private String chapterId;

  private String title;
  private String markdownContent;

  @Builder.Default private BigDecimal minimumScoreToPass = new BigDecimal("70");

  @JsonIgnore @DBRef @Builder.Default private Map<String, Quiz> quiz = new HashMap<>();

  @JsonProperty("quiz")
  public List<Quiz> getOptionsAsList() {
    return new ArrayList<>(quiz.values());
  }

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
