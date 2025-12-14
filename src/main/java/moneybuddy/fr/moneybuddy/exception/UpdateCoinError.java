/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class UpdateCoinError extends MoneyBuddyException {

  public UpdateCoinError(String subAccountId) {
    super(
        ("Le montant dépasse les coins du subAccount: " + subAccountId),
        HttpStatus.BAD_REQUEST,
        "UPDATE_COIN_ERROR");
  }
}
