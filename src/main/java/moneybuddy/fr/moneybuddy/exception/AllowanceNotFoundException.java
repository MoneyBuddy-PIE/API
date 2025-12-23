/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class AllowanceNotFoundException extends MoneyBuddyException {
  public AllowanceNotFoundException(String id) {
    super(
        String.format("Allowance trouvé avec l'id: %s", id),
        HttpStatus.NOT_FOUND,
        "ALLOWANCE_NOT_FOUND");
  }

  public AllowanceNotFoundException() {
    super(String.format("Allowances pas trouvé"), HttpStatus.NOT_FOUND, "ALLOWANCE_NOT_FOUND");
  }
}
