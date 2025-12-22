/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class SettingNotFound extends MoneyBuddyException {
  public SettingNotFound(String id) {
    super(
        String.format("Setting non trouvé avec l'id: %s", id),
        HttpStatus.NOT_FOUND,
        "SETTING_NOT_FOUND");
  }
}
