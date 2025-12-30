/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class UserProgressNotFoundException extends MoneyBuddyException {
  public UserProgressNotFoundException(String id) {
    super(
        String.format("User progress non trouvé avec le subAccountId %s, ", id),
        HttpStatus.NOT_FOUND,
        "USER_PROGRESS_NOT_FOUND");
  }
}
