/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends MoneyBuddyException {
  public AccountNotFoundException(String email) {
    super(
        String.format("Aucun compte trouvé avec l'email: %s", email),
        HttpStatus.NOT_FOUND,
        "ACCOUNT_NOT_FOUND");
  }
}
