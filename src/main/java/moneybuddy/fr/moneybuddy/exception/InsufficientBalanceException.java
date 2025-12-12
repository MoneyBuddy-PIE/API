package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends MoneyBuddyException {
    public InsufficientBalanceException() {
        super(
            "Solde insuffisant pour effectuer cette opération",
            HttpStatus.FORBIDDEN,
            "INSUFFICIENT_BALANCE"
        );
    }

    public InsufficientBalanceException(String message) {
        super(
            message,
            HttpStatus.FORBIDDEN,
            "INSUFFICIENT_BALANCE"
        );
    }
}
