/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.repository.TransactionRepository;
import moneybuddy.fr.moneybuddy.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final SubAccountService subAccountService;
  private final TransactionRepository transactionRepository;
  private final JwtService jwtService;
  private final Utils utils;

  public Page<Transaction> getAllTransactions(
      int page, int size, String sortBy, String sortDir, String accountId, String subAccountId) {
    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);

    Page<Transaction> transactions =
        accountId != null
            ? transactionRepository.findAllByAccountId(accountId, pageable)
            : subAccountId != null
                ? transactionRepository.findAllByChildId(subAccountId, pageable)
                : transactionRepository.findAll(pageable);

    return transactions;
  }

  public Page<Transaction> getTransactions(
      String token,
      String subAccountIdParams,
      boolean isGoal,
      int page,
      int size,
      String sortBy,
      String sortDir) {
    SubAccountRole subAccountRole = jwtService.extractSubAccountRole(token);
    subAccountRole =
        subAccountRole.equals(SubAccountRole.OWNER) ? SubAccountRole.PARENT : subAccountRole;
    String accountId = jwtService.extractSubAccountAccountId(token);
    String subAccountId = jwtService.extractSubAccountId(token);

    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);
    Page<Transaction> transactions;

    if (subAccountRole.equals(SubAccountRole.PARENT)) {
      transactions =
          isGoal
              ? transactionRepository.findByAccountIdAndGoal(accountId, pageable)
              : subAccountIdParams != null
                  ? transactionRepository.findAllByAccountIdAndChildId(
                      accountId, subAccountIdParams, pageable)
                  : transactionRepository.findAllByAccountId(accountId, pageable);
    } else {
      transactions =
          isGoal
              ? transactionRepository.findByChildIdAndGoal(subAccountId, pageable)
              : transactionRepository.findAllByChildId(subAccountId, pageable);
    }

    return transactions;
  }

  public Page<Transaction> getAllTransactionsByAccountId(
      String accountId, int page, int size, String sortBy, String sortDir) {
    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);

    Page<Transaction> transactions = transactionRepository.findAllByAccountId(accountId, pageable);
    return transactions;
  }

  public Page<Transaction> getAllTransactionsBySubAccountId(
      String subAccountId, int page, int size, String sortBy, String sortDir) {
    SubAccount subAccount = subAccountService.getById(subAccountId);
    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);

    return subAccount.getRole().equals(SubAccountRole.CHILD)
        ? transactionRepository.findAllByChildId(subAccountId, pageable)
        : transactionRepository.findAllByParentId(subAccountId, pageable);
  }

  public List<Transaction> getTransactionByGoalId(String goalId) {
    return transactionRepository.findAllByGoalId(goalId);
  }

  public void createTransaction(Transaction transaction) {
    transactionRepository.save(transaction);
  }
}
