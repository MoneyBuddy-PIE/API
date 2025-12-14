/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class NoRight extends MoneyBuddyException {
  public NoRight() {
    super("Vous n avez pas les droits", HttpStatus.FORBIDDEN, "NO_RIGHT");
  }
}
