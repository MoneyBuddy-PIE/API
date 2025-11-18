package moneybuddy.fr.moneybuddy.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.model.DepositDetails;
import moneybuddy.fr.moneybuddy.repository.GoalTransactionRepository;
import moneybuddy.fr.moneybuddy.utils.Utils;

@Service
@RequiredArgsConstructor
public class GoalTransactionService {
    private final GoalTransactionRepository goalTransactionRepository;
    private final Utils utils;

    public ResponseEntity<Page<DepositDetails>> getAllGoalTransactions (
        int page, 
        int size, 
        String sortBy, 
        String sortDir
    ) {
        Pageable pageable = utils.pagination(page, size, sortBy, sortDir);

        Page<DepositDetails> transactions = goalTransactionRepository.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }

    public ResponseEntity<List<DepositDetails>> getSingleGoalTransactions(String goalId) {

        List<DepositDetails> transactions = goalTransactionRepository.findByGoalId(goalId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(transactions);
    }

    public ResponseEntity<Page<DepositDetails>> getAllGoalTransactionsByAccountId(
        String accountId,
        int page, 
        int size,
        String sortBy, 
        String sortDir
    ) {
        Pageable pageable = utils.pagination(page, size, sortBy, sortDir);

        Page<DepositDetails> transactions = goalTransactionRepository.findAllByAccountId(accountId, pageable);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(transactions);
    }

    public ResponseEntity<Page<DepositDetails>> getAllGoalTransactionsByChildId(
        String accountId,
        int page, 
        int size,
        String sortBy, 
        String sortDir
    ) {
        Pageable pageable = utils.pagination(page, size, sortBy, sortDir);

        Page<DepositDetails> transactions = goalTransactionRepository.findAllByChildId(accountId, pageable);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(transactions);
    }

    public ResponseEntity<Page<DepositDetails>> getAllGoalTransactionsByParentId(
        String accountId,
        int page, 
        int size,
        String sortBy, 
        String sortDir
    ) {
        Pageable pageable = utils.pagination(page, size, sortBy, sortDir);

        Page<DepositDetails> transactions = goalTransactionRepository.findAllByParentId(accountId, pageable);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(transactions);
    }
}
