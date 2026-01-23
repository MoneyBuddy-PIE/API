/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.chapter;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.dtos.course.CourseWithProgress;

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

  List<CourseWithProgress> courses;
}
