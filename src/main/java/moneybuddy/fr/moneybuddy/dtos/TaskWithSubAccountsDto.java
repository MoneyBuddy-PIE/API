/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.Income;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.TaskHistory;
import moneybuddy.fr.moneybuddy.model.enums.TaskStatus;
import moneybuddy.fr.moneybuddy.model.enums.TaskType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskWithSubAccountsDto {

  // Champs Task de base
  private String id;
  private String subaccountIdParent;
  private String subaccountIdChild;
  private String accountId;
  private String description;
  private TaskType type;
  private TaskStatus status;
  private boolean preValidate;
  private boolean disable;
  private Income income;
  private List<DayOfWeek> weeklyDays;
  private int monthlyDay;
  private BigDecimal moneyReward;
  private int coinReward;
  private LocalDateTime dateLimit;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Historique de la tâche
  private List<TaskHistory> taskHistory;

  // Données enrichies des sous-comptes
  private SubAccount childSubAccount;
  private SubAccount parentSubAccount;
}
