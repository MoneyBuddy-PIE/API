/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class AccountDesactivated extends MoneyBuddyException {
  public AccountDesactivated(String id) {
    super(
        String.format("Account with id :  %s is desactivated", id),
        HttpStatus.FORBIDDEN,
        "ACCOUNT_DESACTIVATED");
  }
}
