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
public class ChapterWithoutCoursesForAdmin {

  @Id private String id;
  private String accountId;

  private String title;
  private String description;
  private int level;
  private int order;
  private int coinReward;

  private String image_url;

  @Builder.Default private boolean locked = true;

  private SubAccountRole subAccountRole;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;

  private int viewed;
  private int completed;

  public static ChapterWithoutCoursesForAdmin from(Chapter chapter) {
    return ChapterWithoutCoursesForAdmin.builder()
        .id(chapter.getId())
        .accountId(chapter.getAccountId())
        .title(chapter.getTitle())
        .description(chapter.getDescription())
        .level(chapter.getLevel())
        .order(chapter.getOrder())
        .coinReward(chapter.getCoinReward())
        .image_url(chapter.getImage_url())
        .locked(chapter.isLocked())
        .subAccountRole(chapter.getSubAccountRole())
        .viewed(chapter.getViewed())
        .completed(chapter.getCompleted())
        .createdAt(chapter.getCreatedAt())
        .updatedAt(chapter.getUpdatedAt())
        .build();
  }
}
