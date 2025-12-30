/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;

import lombok.*;
import moneybuddy.fr.moneybuddy.model.enums.RessourceType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ressources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ressource {
  @Id String id;
  private String courseId;

  private String title;
  private String url;
  private RessourceType type;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
