/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.course;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.dtos.section.SectionWithProgress;
import moneybuddy.fr.moneybuddy.model.Ressource;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@Builder
public class CourseWithProgress {

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

  @DBRef @Builder.Default private Map<String, SectionWithProgress> sections = new HashMap<>();
  @DBRef @Builder.Default private Map<String, Ressource> ressources = new HashMap<>();
}
