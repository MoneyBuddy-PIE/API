/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionProgress {
  private String sectionId;
  private String courseId;
  private String chapterId;

  private BigDecimal subAccountScore;
  private BigDecimal score;

  private boolean completed;
  private LocalDateTime completedAt;
}
