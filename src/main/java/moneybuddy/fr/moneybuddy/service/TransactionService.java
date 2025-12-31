/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.repository.TransactionRepository;
import moneybuddy.fr.moneybuddy.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final Utils utils;

  public Page<Transaction> getAllTransactions(
      int page, int size, String sortBy, String sortDir, String accountId, String subAccountId) {
    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);

    Page<Transaction> transactions =
        accountId != null
            ? transactionRepository.findAllByAccountId(accountId, pageable)
            : subAccountId != null
                ? transactionRepository.findAllByChildId(pageable, subAccountId)
                : transactionRepository.findAll(pageable);

    return transactions;
  }

  public List<Transaction> getTransactions(String token, String subAccountId, boolean isGoal) {
    List<Transaction> transactions =
        isGoal
            ? transactionRepository.findByChildAndGoal(subAccountId)
            : transactionRepository.findAllByChildId(subAccountId);

    return transactions;
  }

  public Page<Transaction> getAllTransactionsByAccountId(
      String accountId, int page, int size, String sortBy, String sortDir) {
    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);

    Page<Transaction> transactions = transactionRepository.findAllByAccountId(accountId, pageable);
    return transactions;
  }

  public List<Transaction> getTransactionByGoalId(String goalId) {

    List<Transaction> transactions = transactionRepository.findAllByGoalId(goalId);
    return transactions;
  }

  public void createTransaction(Transaction transaction) {
    transactionRepository.save(transaction);
  }
}
