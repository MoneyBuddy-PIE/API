/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class CustomError extends MoneyBuddyException {

  public CustomError(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }
}
