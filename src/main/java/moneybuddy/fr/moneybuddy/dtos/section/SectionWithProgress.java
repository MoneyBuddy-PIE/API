/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.section;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.Quiz;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@Builder
public class SectionWithProgress {

  private String id;
  private String courseId;
  private String chapterId;

  private String title;
  private String markdownContent;

  @Builder.Default private BigDecimal minimumScoreToPass = new BigDecimal("70");

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;

  private boolean completed;

  @JsonIgnore @DBRef @Builder.Default private Map<String, Quiz> quiz = new HashMap<>();

  @JsonProperty("quiz")
  public List<Quiz> getQuizAsList() {
    return new ArrayList<>(quiz.values());
  }
}
