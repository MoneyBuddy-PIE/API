package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends MoneyBuddyException {
    public EmailAlreadyExistsException(String email) {
        super(
            String.format("L'email %s est déjà utilisé", email),
            HttpStatus.CONFLICT,
            "EMAIL_ALREADY_EXISTS"
        );
    }
}
