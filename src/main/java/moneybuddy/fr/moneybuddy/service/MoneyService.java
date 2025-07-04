package moneybuddy.fr.moneybuddy.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.Money.AddMoney;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import moneybuddy.fr.moneybuddy.repository.TransactionRepository;

@Service
@RequiredArgsConstructor
public class MoneyService {
    private final SubAccountRepository subAccountRepository;
    private final TransactionRepository transactionRepository;
    private final JwtService jwtService;

    public ResponseEntity<AuthResponse> response(String message, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(AuthResponse.builder()
                        .error(message)
                        .build());
    }

    public ResponseEntity<AuthResponse> updateMoney(AddMoney request, String token, boolean isAdd) {
        SubAccount subAccount = subAccountRepository.findById(request.getSubAccountId())
                .orElseThrow(() -> new IllegalArgumentException("SubAccount non trouvé"));

        String parentId = jwtService.extractSubAccountAccountId(token);

        double amount;
        try {
            amount = Double.parseDouble(request.getAmount());
        } catch (NumberFormatException e) {
            return response("Montant invalide", HttpStatus.BAD_REQUEST);
        }

        double currentBalance;
        try {
            currentBalance = Double.parseDouble(subAccount.getMoney());
        } catch (NumberFormatException e) {
            return response("Solde du sous-compte invalide", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!isAdd && currentBalance < amount) {
            return response("Fonds insuffisants", HttpStatus.BAD_REQUEST);
        }

        double newBalance = isAdd
                ? currentBalance + amount
                : currentBalance - amount;

        subAccount.setMoney(String.valueOf(newBalance));
        subAccountRepository.save(subAccount);

        Transaction transaction = Transaction.builder()
                .amount(String.valueOf(amount))
                .childId(request.getSubAccountId())
                .parentId(parentId)
                .description(request.getDescription())
                .type(isAdd ? "CREDIT" : "DEBIT")
                .build();
        transactionRepository.save(transaction);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    public ResponseEntity<?> getSubAccountBalance(String subAccountId) {
        return subAccountRepository.findById(subAccountId)
            .<ResponseEntity<?>>map(subAccount -> ResponseEntity.ok().body(subAccount.getMoney()))
            .orElseGet(() -> response("SubAccount non trouvé", HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<?> getAllTransactions(String subAccountId) {
        boolean exists = subAccountRepository.existsById(subAccountId);
        if (!exists) {
            return response("SubAccount non trouvé", HttpStatus.NOT_FOUND);
        }

        var transactions = transactionRepository.findByChildId(subAccountId);
        return ResponseEntity.ok(transactions);
    }
}
