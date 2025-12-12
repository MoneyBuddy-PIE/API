/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class InvalidPinException extends MoneyBuddyException {
  public InvalidPinException() {
    super("Code PIN incorrect", HttpStatus.UNAUTHORIZED, "INVALID_PIN");
  }

  public InvalidPinException(String message) {
    super(message, HttpStatus.UNAUTHORIZED, "INVALID_PIN");
  }
}
