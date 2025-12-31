/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.chapter;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChapterWithoutCoursesWithProgress {
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
}
