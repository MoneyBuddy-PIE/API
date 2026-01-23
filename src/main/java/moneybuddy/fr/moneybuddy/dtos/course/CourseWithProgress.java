/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.course;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.dtos.section.SectionWithProgress;
import moneybuddy.fr.moneybuddy.model.Ressource;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
public class CourseWithProgress {

  private String id;
  private String chapterId;

  private String title;
  private int order;
  private int coinReward;
  private String imageUrl;
  private int readTime;

  private boolean locked;

  private boolean completed;
  private int completedCoursesCount;
  private int totalCoursesCount;
  private int progressPercentage;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;


  @JsonIgnore @DBRef @Builder.Default private Map<String, SectionWithProgress> sections = new HashMap<>();
  @JsonIgnore @DBRef @Builder.Default private Map<String, Ressource> ressources = new HashMap<>();

  @JsonProperty("sections")
  public List<SectionWithProgress> getSectionAsList() {
    return new ArrayList<>(sections.values());
  }

  @JsonProperty("ressources")
  public List<Ressource> getRessourceAsList() {
    return new ArrayList<>(ressources.values());
  }
}
