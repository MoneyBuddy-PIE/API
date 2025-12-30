/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "courses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
  @Id private String id;
  private String chapterId;
  private String accountId;

  private String image_url;
  private String title;
  private int readTime;
  private int order;
  private int coinReward;

  @DBRef @Builder.Default private Map<String, Ressource> ressource = new HashMap<>();
  @DBRef @Builder.Default private Map<String, Section> sections = new HashMap<>();

  @Builder.Default private boolean locked = true;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;

  private int viewed;
  private int completed;
}
