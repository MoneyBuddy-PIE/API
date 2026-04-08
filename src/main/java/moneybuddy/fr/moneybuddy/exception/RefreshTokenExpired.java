/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class RefreshTokenExpired extends MoneyBuddyException {

  public RefreshTokenExpired() {
    super(
        String.format("Refresh token expired"),
        HttpStatus.EXPECTATION_FAILED,
        "REFRESH_TOKEN_ERROR");
  }
}
