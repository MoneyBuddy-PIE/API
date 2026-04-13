/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.chapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.enums.ChapterCategory;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.data.annotation.Id;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChapterWithCoursesForAdmin {
  @Id private String id;
  private String accountId;

  private String title;
  private String description;
  private Integer level;
  private Integer order;
  private Integer coinReward;

  private String image_url;

  @Builder.Default private boolean locked = true;

  private SubAccountRole subAccountRole;
  private List<ChapterCategory> category;

  @Builder.Default public List<Course> courses = new ArrayList<>();

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;

  private int viewed;
  private int completed;

  public static ChapterWithCoursesForAdmin from(Chapter chapter) {
    ChapterWithCoursesForAdmin chapterWithCoursesForAdmin =
        ChapterWithCoursesForAdmin.builder()
            .id(chapter.getId())
            .accountId(chapter.getAccountId())
            .title(chapter.getTitle())
            .description(chapter.getDescription())
            .image_url(chapter.getImage_url())
            .locked(chapter.isLocked())
            .subAccountRole(chapter.getSubAccountRole())
            .category(chapter.getCategory())
            .viewed(chapter.getViewed())
            .completed(chapter.getCompleted())
            .createdAt(chapter.getCreatedAt())
            .updatedAt(chapter.getUpdatedAt())
            .courses(chapter.getCoursesAsList())
            .build();

    if (SubAccountRole.CHILD.equals(chapter.getSubAccountRole())) {
      chapterWithCoursesForAdmin.setOrder(chapter.getOrder());
      chapterWithCoursesForAdmin.setCoinReward(chapter.getCoinReward());
      chapterWithCoursesForAdmin.setLevel(chapter.getLevel());
    }

    return chapterWithCoursesForAdmin;
  }
}
