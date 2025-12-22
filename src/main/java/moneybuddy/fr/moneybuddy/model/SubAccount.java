/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subAccounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "setting")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubAccount {
  @Id @EqualsAndHashCode.Include private String id;
  private String accountId;

  private String name;

  @DBRef private Setting setting;

  @Builder.Default private SubAccountRole role = SubAccountRole.CHILD;

  @Builder.Default private boolean isActive = false;

  @Builder.Default private BigDecimal money = BigDecimal.ZERO.setScale(2);
  @Builder.Default private BigDecimal income = BigDecimal.ZERO.setScale(2);
  @Builder.Default private int coin = 0;

  @Builder.Default private String iconStyle = "bottts-neutral";
  @Builder.Default private String iconName = "Mason";

  @JsonIgnore private String pin;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
  private LocalDateTime lastConnexion;
}
