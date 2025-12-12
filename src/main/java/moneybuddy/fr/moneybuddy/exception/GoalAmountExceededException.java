/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class GoalAmountExceededException extends MoneyBuddyException {
  public GoalAmountExceededException() {
    super(
        "Le montant ajouté dépasse le montant cible de l'objectif",
        HttpStatus.FORBIDDEN,
        "GOAL_AMOUNT_EXCEEDED");
  }

  public GoalAmountExceededException(
      Float currentAmount, Float targetAmount, Float attemptedAmount) {
    super(
        String.format(
            "Le montant dépasse l'objectif. Actuel: %.2f€, Cible: %.2f€, Tentative d'ajout: %.2f€",
            currentAmount, targetAmount, attemptedAmount),
        HttpStatus.FORBIDDEN,
        "GOAL_AMOUNT_EXCEEDED");
  }
}
