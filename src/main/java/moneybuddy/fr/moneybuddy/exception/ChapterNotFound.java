/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class ChapterNotFound extends MoneyBuddyException {
  public ChapterNotFound(String id) {
    super(
        String.format("Chapitre non trouvé avec l'id: %s", id),
        HttpStatus.NOT_FOUND,
        "CHAPTER_NOT_FOUND");
  }

  public ChapterNotFound() {
    super(String.format("Chapitres non trouvé"), HttpStatus.NOT_FOUND, "CHAPTERS_NOT_FOUND");
  }
}
