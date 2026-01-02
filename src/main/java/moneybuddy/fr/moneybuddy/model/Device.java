/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.enums.DevicePlatform;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "devices")
@Data
@Builder
public class Device {
  @Id String id;
  private String accountId;
  private String subAccountId;

  private String userId;
  private String token;
  private DevicePlatform platform;

  @Builder.Default private boolean activated = false;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
