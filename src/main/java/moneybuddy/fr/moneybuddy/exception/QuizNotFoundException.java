/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class QuizNotFoundException extends MoneyBuddyException {

  public QuizNotFoundException(String id) {
    super(
        String.format("Quiz non trouvé avec l'id: %s", id), HttpStatus.NOT_FOUND, "QUIZ_NOT_FOUND");
  }
}
