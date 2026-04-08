/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class SubAccountDesactivated extends MoneyBuddyException {
  public SubAccountDesactivated(String id) {
    super(
        String.format("SubAccount with id :  %s is desactivated", id),
        HttpStatus.FORBIDDEN,
        "SUBACCOUNT_DESACTIVATED");
  }
}
