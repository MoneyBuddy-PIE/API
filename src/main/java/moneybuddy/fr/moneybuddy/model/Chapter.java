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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chapters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
  @Id private String id;
  private String accountId;

  private String title;
  private String description;
  private int level;

  private int order;

  private int coinReward;

  private String image_url;

  @Builder.Default private boolean locked = true;

  @JsonIgnore @DBRef @Builder.Default private Map<String, Course> courses = new HashMap<>();

  @JsonProperty("courses")
  public List<Course> getCoursesAsList() {
    return new ArrayList<>(courses.values());
  }

  private SubAccountRole subAccountRole;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;

  @Builder.Default private int viewed = 0;
  @Builder.Default private int completed = 0;
}
