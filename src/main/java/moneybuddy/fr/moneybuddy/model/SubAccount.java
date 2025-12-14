/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subAccounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccount {
  @Id private String id;
  private String accountId;

  private String name;

  @Builder.Default private SubAccountRole role = SubAccountRole.CHILD;

  @Builder.Default private boolean isActive = false;

  @Builder.Default private String money = "0";
  @Builder.Default private int coin = 0;

  @Builder.Default private String iconStyle = "bottts-neutral";
  @Builder.Default private String iconName = "Mason";

  @JsonIgnore private String pin;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
  private LocalDateTime lastConnexion;
}
