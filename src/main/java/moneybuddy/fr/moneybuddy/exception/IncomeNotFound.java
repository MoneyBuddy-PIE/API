/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class IncomeNotFound extends MoneyBuddyException {
  public IncomeNotFound(String incomeId) {
    super(
        String.format("Income non trouvé avec l'id: %s", incomeId),
        HttpStatus.NOT_FOUND,
        "INCOME_NOT_FOUND");
  }

  public IncomeNotFound() {
    super(String.format("Incomes non trouvé"), HttpStatus.NOT_FOUND, "INCOMES_NOT_FOUND");
  }
}
