package moneybuddy.fr.moneybuddy.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.Money.AddMoney;
import moneybuddy.fr.moneybuddy.dtos.Money.WithdrawlMoney;
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

    public ResponseEntity<AuthResponse> addMoney (AddMoney request, String token) {
        SubAccount subAccount = subAccountRepository.findById(request.getSubAccountId()).orElseThrow();
        String parentId = jwtService.extractSubAccountAccountId(token);

        Transaction transaction = Transaction.builder()
            .amount(request.getAmount())
            .childId(request.getSubAccountId())
            .parentId(parentId)
            .build();
        transactionRepository.save(transaction);

        subAccount.setMoney(subAccount.getMoney() + request.getAmount());
        subAccountRepository.save(subAccount);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

        public ResponseEntity<AuthResponse> withdrawMoney (WithdrawlMoney request, String token) {
        SubAccount subAccount = subAccountRepository.findById(request.getSubAccountId()).orElseThrow();
        String parentId = jwtService.extractSubAccountAccountId(token);

        Transaction transaction = Transaction.builder()
            .amount(request.getAmount())
            .childId(request.getSubAccountId())
            .parentId(parentId)
            .build();
        transactionRepository.save(transaction);

        subAccount.setMoney(subAccount.getMoney() - request.getAmount());
        subAccountRepository.save(subAccount);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }
}
