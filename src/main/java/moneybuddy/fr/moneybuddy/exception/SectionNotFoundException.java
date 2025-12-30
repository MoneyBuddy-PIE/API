/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class SectionNotFoundException extends MoneyBuddyException {

  public SectionNotFoundException(String id) {
    super(
        String.format("Section non trouvé avec l'id: %s", id),
        HttpStatus.NOT_FOUND,
        "SECTION_NOT_FOUND");
  }

  public SectionNotFoundException() {
    super(String.format("Sections non trouvé"), HttpStatus.NOT_FOUND, "SECTIONS_NOT_FOUND");
  }
}
