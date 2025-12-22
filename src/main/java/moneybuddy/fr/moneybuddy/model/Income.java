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
import moneybuddy.fr.moneybuddy.model.enums.IncomeStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "incomes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Income {
  @Id private String id;
  private String accountId;
  private String subAccountId;
  private String subAccountIdChild;

  @DBRef private SubAccount subAccount;
  @DBRef private Task task;

  private BigDecimal amount;
  @Builder.Default private IncomeStatus status = IncomeStatus.PENDING;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
