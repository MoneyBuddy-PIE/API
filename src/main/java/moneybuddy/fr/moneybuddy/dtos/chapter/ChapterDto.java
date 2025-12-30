/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.chapter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDto {
  @Id private String id;
  private String accountId;

  private String title;
  private String description;
  private int level;
  private int order;
  private int coinReward;

  private String image_url;

  @Builder.Default private boolean locked = true;
  @DBRef @Builder.Default private Map<String, Course> courses = new HashMap<>();

  private SubAccountRole subAccountRole;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;

  public static ChapterDto from(Chapter chapter) {
    return ChapterDto.builder()
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
        .createdAt(chapter.getCreatedAt())
        .updatedAt(chapter.getUpdatedAt())
        .courses(chapter.getCourses())
        .build();
  }
}
