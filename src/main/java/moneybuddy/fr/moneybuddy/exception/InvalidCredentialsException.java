/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends MoneyBuddyException {
  public InvalidCredentialsException() {
    super("Email ou mot de passe incorrect", HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
  }
}
