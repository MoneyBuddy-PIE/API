/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.course;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.Ressource;

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

  private List<Ressource> ressource;

  private boolean completed;
  private int completedCoursesCount;
  private int totalCoursesCount;
  private int progressPercentage;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
