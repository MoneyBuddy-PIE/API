package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.Money.AddMoney;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.model.enums.TransactionType;
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

        if (SubAccountRole.CHILD.equals(jwtService.extractSubAccountRole(token))) {
            return response("Vous n etes pas un authorizé", HttpStatus.FORBIDDEN);
        }

        String parentId = jwtService.extractSubAccountAccountId(token);

        double amount;
        try {
            amount = Double.parseDouble(request.getAmount());
        } catch (NumberFormatException e) {
            return response("Montant invalide", HttpStatus.BAD_REQUEST);
        }

        double currentBalance = subAccount.getMoney() == null 
            ? 0.0 
            : Double.parseDouble(subAccount.getMoney());

        if (!isAdd && currentBalance < amount) {
            return response("Fonds insuffisants", HttpStatus.BAD_REQUEST);
        }

        double newBalance = isAdd
                ? currentBalance + amount
                : currentBalance - amount;

        subAccount.setMoney(String.valueOf(newBalance));
        subAccountRepository.save(subAccount);

        Transaction transaction = Transaction.builder()
                .childId(request.getSubAccountId())
                .parentId(parentId)
                .amount(String.valueOf(amount))
                .oldAmount(String.valueOf(currentBalance))
                .newAmount(String.valueOf(newBalance))
                .description(request.getDescription())
                .type(isAdd ? TransactionType.CREDIT : TransactionType.DEBIT)
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }
}
