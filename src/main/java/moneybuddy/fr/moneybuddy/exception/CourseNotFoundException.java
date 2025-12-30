/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class CourseNotFoundException extends MoneyBuddyException {

  public CourseNotFoundException(String id) {
    super(
        String.format("Course non trouvé avec l'id: %s", id),
        HttpStatus.NOT_FOUND,
        "COURSE_NOT_FOUND");
  }

  public CourseNotFoundException() {
    super(String.format("Course non trouvé"), HttpStatus.NOT_FOUND, "COURSES_NOT_FOUND");
  }
}
