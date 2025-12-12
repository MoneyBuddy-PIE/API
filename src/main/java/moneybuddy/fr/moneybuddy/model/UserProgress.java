/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userProgress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {
  @Id private String id;

  @Builder.Default private Map<String, CourseProgress> courseProgress = new HashMap<>();

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();

  @Builder.Default private LocalDateTime updatedAt = LocalDateTime.now();

  private String subAccountId;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CourseProgress {
    @Builder.Default private boolean completed = false;

    private int quizScore;

    @Builder.Default private boolean unlocked = false;

    @Builder.Default private LocalDateTime completedAt = null;
  }
}
