/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.chapter;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.dtos.course.CourseWithProgress;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;

@Data
@Builder
public class ChapterWithProgress {
  private String id;

  private String title;
  private String description;
  private String imageUrl;
  private boolean locked;

  private SubAccountRole subAccountRole;

  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  private Integer level;

  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  private Integer order;

  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  private Integer coinReward;

  private boolean completed;
  private int completedCoursesCount;
  private int totalCoursesCount;
  private int progressPercentage;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;

  List<CourseWithProgress> courses;
}
