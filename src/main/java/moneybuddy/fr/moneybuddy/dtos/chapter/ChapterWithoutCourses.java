/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.chapter;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class ChapterWithoutCourses {
  @Id private String id;
  private String accountId;

  private String title;
  private String description;
  private int level;
  private int order;
  private int coinReward;
  private int totalCoursesCount;

  private String image_url;

  @Builder.Default private boolean locked = true;

  private SubAccountRole subAccountRole;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;

  public static ChapterWithoutCourses from(Chapter chapter) {
    return ChapterWithoutCourses.builder()
        .id(chapter.getId())
        .accountId(chapter.getAccountId())
        .title(chapter.getTitle())
        .description(chapter.getDescription())
        .level(chapter.getLevel())
        .order(chapter.getOrder())
        .totalCoursesCount(chapter.getCourses().size())
        .coinReward(chapter.getCoinReward())
        .image_url(chapter.getImage_url())
        .locked(chapter.isLocked())
        .subAccountRole(chapter.getSubAccountRole())
        .createdAt(chapter.getCreatedAt())
        .updatedAt(chapter.getUpdatedAt())
        .build();
  }
}
