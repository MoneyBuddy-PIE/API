/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonIgnore @DBRef @Builder.Default private Map<String, Ressource> ressource = new HashMap<>();

  @JsonProperty("ressource")
  public List<Ressource> getRessourceAsList() {
    return new ArrayList<>(ressource.values());
  }

  @JsonIgnore @DBRef @Builder.Default private Map<String, Section> sections = new HashMap<>();

  @JsonProperty("sections")
  public List<Section> getSectionAsList() {
    return new ArrayList<>(sections.values());
  }

  @Builder.Default private boolean locked = true;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;

  private int viewed;
  private int completed;
}
