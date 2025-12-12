package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class GoalAlreadyCompletedException extends MoneyBuddyException {
    public GoalAlreadyCompletedException() {
        super(
            "L'objectif d'épargne est déjà terminé",
            HttpStatus.CONFLICT,
            "GOAL_ALREADY_COMPLETED"
        );
    }

    public GoalAlreadyCompletedException(String goalName) {
        super(
            String.format("L'objectif d'épargne '%s' est déjà terminé", goalName),
            HttpStatus.CONFLICT,
            "GOAL_ALREADY_COMPLETED"
        );
    }
}
