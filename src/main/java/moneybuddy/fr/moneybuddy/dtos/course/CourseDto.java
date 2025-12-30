/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.course;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.*;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.Ressource;
import moneybuddy.fr.moneybuddy.model.Section;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
  @Id private String id;
  private String chapterId;
  private String accountId;

  @DBRef Account account;

  private String image_url;
  private String title;
  private int readTime;
  private int order;
  private int coinReward;

  @DBRef @Builder.Default private Map<String, Ressource> ressources = new HashMap<>();
  @DBRef @Builder.Default private Map<String, Section> sections = new HashMap<>();

  @Builder.Default private boolean locked = true;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;

  public static CourseDto from(Course course) {
    return CourseDto.builder()
        .id(course.getId())
        .accountId(course.getAccountId())
        .chapterId(course.getChapterId())
        .title(course.getTitle())
        .image_url(course.getImage_url())
        .readTime(course.getReadTime())
        .order(course.getOrder())
        .coinReward(course.getCoinReward())
        .locked(course.isLocked())
        .sections(course.getSections())
        .createdAt(course.getCreatedAt())
        .updatedAt(course.getUpdatedAt())
        .ressources(course.getRessource())
        .build();
  }
}
