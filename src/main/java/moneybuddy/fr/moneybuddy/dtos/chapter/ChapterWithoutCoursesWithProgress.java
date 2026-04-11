/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.chapter;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.enums.ChapterCategory;

@Data
@Builder
public class ChapterWithoutCoursesWithProgress {
  private String id;

  private String title;
  private String description;
  private String imageUrl;
  private List<ChapterCategory> category;
  private boolean locked;

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
}
