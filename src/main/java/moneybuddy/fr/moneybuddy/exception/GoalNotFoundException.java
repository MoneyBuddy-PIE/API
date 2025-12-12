package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class GoalNotFoundException extends MoneyBuddyException {
    public GoalNotFoundException(String goalId) {
        super(
            String.format("Objectif d'épargne non trouvé avec l'id: %s", goalId),
            HttpStatus.NOT_FOUND,
            "GOAL_NOT_FOUND"
        );
    }

    public GoalNotFoundException() {
        super(
            "Objectif d'épargne non trouvé",
            HttpStatus.NOT_FOUND,
            "GOAL_NOT_FOUND"
        );
    }
}
