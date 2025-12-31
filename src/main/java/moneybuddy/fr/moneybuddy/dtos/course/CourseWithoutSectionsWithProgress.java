/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.course;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseWithoutSectionsWithProgress {
  private String id;
  private String chapterId;

  private String title;
  private int order;
  private int coinReward;
  private String imageUrl;
  private int readTime;

  private boolean locked;

  private boolean completed;
  private int completedCoursesCount;
  private int totalCoursesCount;
  private int progressPercentage;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
