/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;

public class GoalNotCompletedException extends MoneyBuddyException {
  public GoalNotCompletedException() {
    super(
        "L'objectif d'épargne n'est pas encore atteint",
        HttpStatus.FORBIDDEN,
        "GOAL_NOT_COMPLETED");
  }

  public GoalNotCompletedException(
      String goalName, BigDecimal currentAmount, BigDecimal targetAmount) {
    super(
        String.format(
            "L'objectif '%s' n'est pas encore atteint. Montant actuel: %.2f€ / %.2f€",
            goalName, currentAmount, targetAmount),
        HttpStatus.FORBIDDEN,
        "GOAL_NOT_COMPLETED");
  }
}
