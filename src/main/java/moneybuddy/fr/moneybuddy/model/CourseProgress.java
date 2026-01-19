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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgress {
  private String courseId;
  private String chapterId;

  @JsonIgnore @Builder.Default
  private Map<String, SectionProgress> sectionProgress = new HashMap<>();

  @JsonProperty("sectionProgress")
  public List<SectionProgress> getSectionProgressAsList() {
    return new ArrayList<>(sectionProgress.values());
  }

  private boolean completed;
  private LocalDateTime completedAt;
}
