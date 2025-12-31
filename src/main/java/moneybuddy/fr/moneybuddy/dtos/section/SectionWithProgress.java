/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.section;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.Quiz;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@Builder
public class SectionWithProgress {

  private String id;
  private String courseId;
  private String chapterId;

  private String title;
  private String markdownContent;

  @Builder.Default private BigDecimal minimumScoreToPass = new BigDecimal("70");

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;

  private boolean completed;

  @DBRef @Builder.Default private Map<String, Quiz> quiz = new HashMap<>();
}
