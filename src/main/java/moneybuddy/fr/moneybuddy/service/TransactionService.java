/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.exception.GoalNotFoundException;
import moneybuddy.fr.moneybuddy.exception.UnauthorizedGoalAccessException;
import moneybuddy.fr.moneybuddy.model.Goal;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.model.enums.Role;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.repository.GoalRepository;
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
  private final GoalRepository goalRepository;
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

  public Page<Transaction> getAllTransactionsBySubAccountId(
      String subAccountId, int page, int size, String sortBy, String sortDir) {
    SubAccount subAccount = subAccountService.getById(subAccountId);
    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);

    return subAccount.getRole().equals(SubAccountRole.CHILD)
        ? transactionRepository.findAllByChildId(subAccountId, pageable)
        : transactionRepository.findAllByParentId(subAccountId, pageable);
  }

  public List<Transaction> getTransactionByGoalId(String token, String goalId) {
    Goal goal = goalRepository.findById(goalId)
        .orElseThrow(() -> new GoalNotFoundException(goalId));

    boolean isAdmin = Role.ADMIN.equals(jwtService.extractAccountRole(token));
    String tokenAccountId = jwtService.extractSubAccountAccountId(token);
    String tokenSubAccountId = jwtService.extractSubAccountId(token);
    SubAccountRole subAccountRole = jwtService.extractSubAccountRole(token);

    boolean isOwner = goal.getAccountId().equals(tokenAccountId);
    boolean isChild = SubAccountRole.CHILD.equals(subAccountRole)
        && goal.getSubaccountIdChild().equals(tokenSubAccountId);

    if (!isAdmin && !isOwner && !isChild) {
      throw new UnauthorizedGoalAccessException(
          "Vous n'avez pas accès aux transactions de cet objectif");
    }

    return transactionRepository.findAllByGoalId(goalId);
  }

  public void createTransaction(Transaction transaction) {
    transactionRepository.save(transaction);
  }
}
