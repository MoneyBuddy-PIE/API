package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class UpdateMoneyError extends MoneyBuddyException{

    
  public UpdateMoneyError(String subAccountId, String text) {
    super(
        (text + " du subAccount: " + subAccountId),
        HttpStatus.BAD_REQUEST,
        "UPDATE_MONEY_ERROR");
  }
}
