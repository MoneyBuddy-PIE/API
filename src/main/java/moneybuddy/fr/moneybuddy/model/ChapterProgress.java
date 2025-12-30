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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProgress {
  private String chapterId;

  @Builder.Default private Map<String, CourseProgress> courseProgress = new HashMap<>();

  private boolean completed;
  private LocalDateTime completedAt;
}
