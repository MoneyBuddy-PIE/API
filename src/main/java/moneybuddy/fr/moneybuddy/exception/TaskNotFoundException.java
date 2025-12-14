/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends MoneyBuddyException {
  public TaskNotFoundException(String taskId) {
    super(
        String.format("Tache non trouvé avec l'id: %s", taskId),
        HttpStatus.NOT_FOUND,
        "TASK_NOT_FOUND");
  }

  public TaskNotFoundException() {
    super("Tache d'épargne non trouvé", HttpStatus.NOT_FOUND, "TASK_NOT_FOUND");
  }
}
