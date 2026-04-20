/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.utils.operations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import moneybuddy.fr.moneybuddy.dtos.Money.AddMoney;
import moneybuddy.fr.moneybuddy.model.Goal;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.model.enums.GoalStatus;
import moneybuddy.fr.moneybuddy.model.enums.TransactionType;
import moneybuddy.fr.moneybuddy.repository.TransactionRepository;
import moneybuddy.fr.moneybuddy.service.MoneyService;
import org.springframework.stereotype.Component;

@Component
public class Operations {

  private final TransactionRepository transactionRepository;
  private final MoneyService moneyService;

  public Operations(TransactionRepository transactionRepository, MoneyService moneyService) {
    this.moneyService = moneyService;
    this.transactionRepository = transactionRepository;
  }

  public void updateProgression(Goal goal, BigDecimal depositStatement) {
    BigDecimal updateProgression =
        depositStatement
            .multiply(BigDecimal.valueOf(100))
            .divide(goal.getAmount(), 2, RoundingMode.HALF_UP);
    if (updateProgression.compareTo(BigDecimal.valueOf(100)) >= 0) {
      goal.setGoalStatus(GoalStatus.DONE);
    }

    goal.setProgression(updateProgression);
  }

  public void updateGoalTransactionHistory(
      Goal goal, TransactionType type, BigDecimal amount, BigDecimal updatedGoalAmount) {
    Transaction transaction =
        Transaction.builder()
            .goalId(goal.getId())
            .childId(goal.getSubaccountIdChild())
            .accountId(goal.getAccountId())
            .amount(String.valueOf(amount))
            .oldAmount(String.valueOf(goal.getDepositStatement()))
            .newAmount(String.valueOf(updatedGoalAmount))
            .description(
                TransactionType.CREDIT.equals(type)
                    ? "Add money to goal: " + goal.getName()
                    : "Remove money from goal: " + goal.getName())
            .type(type)
            .createdAt(LocalDateTime.now())
            .build();

    transactionRepository.save(transaction);
  }

  public void updateAccountBalanceMoney(
      SubAccount subAccount,
      String token,
      BigDecimal money,
      boolean transferAddMoney,
      String description) {
    AddMoney newAddMoneyDetail =
        AddMoney.builder()
            .subAccountId(subAccount.getId())
            .amount(money)
            .description(description)
            .build();

    moneyService.updateMoney(newAddMoneyDetail, token, transferAddMoney);
  }
}
