/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MoneyBuddyException extends RuntimeException {

  private final HttpStatus status;
  private final String errorCode;

  public MoneyBuddyException(String message, HttpStatus status, String errorCode) {
    super(message);
    this.status = status;
    this.errorCode = errorCode;
  }

  public MoneyBuddyException(String message, HttpStatus status) {
    this(message, status, null);
  }
}
