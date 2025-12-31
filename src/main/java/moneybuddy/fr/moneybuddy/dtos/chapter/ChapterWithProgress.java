/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.chapter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.Course;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@Builder
public class ChapterWithProgress {
  private String id;
  private String title;
  private String description;
  private int level;
  private int order;
  private int coinReward;
  private String imageUrl;
  private boolean locked;

  private boolean completed;
  private int completedCoursesCount;
  private int totalCoursesCount;
  private int progressPercentage;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;

  @DBRef @Builder.Default private Map<String, Course> courses = new HashMap<>();
}
