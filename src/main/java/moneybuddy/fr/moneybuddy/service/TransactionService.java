package moneybuddy.fr.moneybuddy.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.repository.TransactionRepository;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public ResponseEntity<List<Transaction>> getAllTransactions(String token, String subAccountId) {
        
        List<Transaction> transactions = transactionRepository.findByChildId(subAccountId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(transactions);
    }
}
