package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class SubAccountNotFoundException extends MoneyBuddyException {
    public SubAccountNotFoundException(String subAccountId) {
        super(
            String.format("Sous-compte non trouvé avec l'id: %s", subAccountId),
            HttpStatus.NOT_FOUND,
            "SUB_ACCOUNT_NOT_FOUND"
        );
    }
}
