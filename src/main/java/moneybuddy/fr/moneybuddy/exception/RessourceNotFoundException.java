/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class RessourceNotFoundException extends MoneyBuddyException {
  public RessourceNotFoundException(String id) {
    super(
        String.format("Ressource non trouvé avec l'id: %s", id),
        HttpStatus.NOT_FOUND,
        "RESSOURCE_NOT_FOUND");
  }
}
