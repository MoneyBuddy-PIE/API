/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends MoneyBuddyException {
  public PasswordMismatchException() {
    super("Les mots de passe ne correspondent pas", HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH");
  }
}
