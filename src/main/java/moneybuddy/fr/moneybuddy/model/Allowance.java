/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.enums.AllowanceFrequency;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "allowances")
@CompoundIndexes({
  @CompoundIndex(name = "allowance_cron_idx", def = "{'active': 1, 'nextExecution': 1}")
})
@Data
@Builder
public class Allowance {

  @Id private String id;
  private String accountId;
  private String subAccountId;
  private String subAccountIdChild;

  @DBRef private SubAccount subAccount;

  private AllowanceFrequency frequency;
  private BigDecimal amount;

  private DayOfWeek weeklyDay;
  private LocalDate startDate;
  private LocalDate nextExecution;

  @Builder.Default private boolean active = true;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
