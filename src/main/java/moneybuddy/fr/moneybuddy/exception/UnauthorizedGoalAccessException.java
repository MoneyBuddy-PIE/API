/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedGoalAccessException extends MoneyBuddyException {
  public UnauthorizedGoalAccessException() {
    super(
        "Vous n'avez pas les droits d'accès pour cet objectif",
        HttpStatus.UNAUTHORIZED,
        "UNAUTHORIZED_GOAL_ACCESS");
  }

  public UnauthorizedGoalAccessException(String message) {
    super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_GOAL_ACCESS");
  }
}
