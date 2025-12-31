/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userProgress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {
  @Id private String id;
  private String accountId;

  @Indexed(unique = true)
  private String subAccountId;

  @Builder.Default private Map<String, ChapterProgress> chapterProgress = new HashMap<>();

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
