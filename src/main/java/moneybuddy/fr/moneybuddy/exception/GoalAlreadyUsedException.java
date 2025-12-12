/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class GoalAlreadyUsedException extends MoneyBuddyException {
  public GoalAlreadyUsedException() {
    super(
        "L'objectif d'épargne a déjà été utilisé et ne peut plus être modifié",
        HttpStatus.FORBIDDEN,
        "GOAL_ALREADY_USED");
  }

  public GoalAlreadyUsedException(String goalName) {
    super(
        String.format("L'objectif '%s' a déjà été utilisé et ne peut plus être modifié", goalName),
        HttpStatus.FORBIDDEN,
        "GOAL_ALREADY_USED");
  }
}
