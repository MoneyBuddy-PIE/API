/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class TaskHistoryNotFounded extends MoneyBuddyException {

  public TaskHistoryNotFounded() {
    super("Objectif d'épargne non trouvé", HttpStatus.NOT_FOUND, "GOAL_NOT_FOUND");
  }
}
