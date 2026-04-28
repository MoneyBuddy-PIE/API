/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.Income.CollectAllIncome;
import moneybuddy.fr.moneybuddy.dtos.Income.UpdateIncomeRequest;
import moneybuddy.fr.moneybuddy.exception.IncomeNotFound;
import moneybuddy.fr.moneybuddy.exception.NoRight;
import moneybuddy.fr.moneybuddy.exception.SubAccountNotFoundException;
import moneybuddy.fr.moneybuddy.model.Allowance;
import moneybuddy.fr.moneybuddy.model.Income;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.model.enums.IncomeStatus;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.enums.TransactionCategory;
import moneybuddy.fr.moneybuddy.model.enums.TransactionType;
import moneybuddy.fr.moneybuddy.repository.IncomeRepository;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeService {

  private final MongoTemplate mongoTemplate;
  private final IncomeRepository incomeRepository;
  private final SubAccountRepository subAccountRepository;
  private final TransactionService transactionService;
  private final JwtService jwtService;

  public void createTransactionForIncome(Income income) {
    Transaction transaction =
        Transaction.builder()
            .accountId(income.getAccountId())
            .childId(income.getSubAccountIdChild())
            .parentId(income.getSubAccountId())
            .type(
                income.getStatus().equals(IncomeStatus.ACCEPTED)
                    ? TransactionType.CREDIT
                    : TransactionType.DEBIT)
            .category(TransactionCategory.MONEY)
            .amount(income.getAmount().toString())
            .description(income.getTask().getDescription())
            .incomeId(income.getId())
            .build();

    transactionService.createTransaction(transaction);
  }

  public List<Income> getAllIncomes(
      String token, String childId, String parentId, IncomeStatus status) {
    SubAccountRole role = jwtService.extractSubAccountRole(token);
    String subAccountId = jwtService.extractSubAccountId(token);
    String accountId = jwtService.extractSubAccountAccountId(token);

    if (role.equals(SubAccountRole.CHILD))
      return incomeRepository
          .findAllBySubAccountIdChild(subAccountId)
          .orElseThrow(() -> new IncomeNotFound());

    Criteria criteria = new Criteria();
    if (status != null) criteria.and("status").is(status);
    if (childId != null) criteria.and("subAccountIdChild").is(childId);
    if (parentId != null) criteria.and("subAccountId").is(parentId);
    criteria.and("accountId").is(accountId);

    Query query = new Query(criteria);
    List<Income> incomes = mongoTemplate.find(query, Income.class);

    return incomes;
  }

  public Income getIncome(String id) {
    Income income = incomeRepository.findById(id).orElseThrow(() -> new IncomeNotFound(id));
    return income;
  }

  public void deleteIncome(String id) {
    getIncome(id);
    incomeRepository.deleteById(id);
  }

  private Income createIncomeFromSource(Object source, SubAccount subAccount) {
    Income income =
        Income.builder().subAccount(subAccount).subAccountIdChild(subAccount.getId()).build();

    if (source instanceof Task task) {
      income.setTask(task);
      income.setAmount(task.getMoneyReward());
      income.setAccountId(task.getAccountId());
      income.setSubAccountId(task.getSubaccountIdParent());
    } else if (source instanceof Allowance allowance) {
      income.setAllowance(allowance);
      income.setAmount(allowance.getAmount());
      income.setAccountId(allowance.getAccountId());
      income.setSubAccountId(allowance.getSubAccountId());
    }
    return incomeRepository.save(income);
  }

  public void increaseSubAccountIncome(SubAccount subAccount, BigDecimal amount, Object source) {
    subAccount.setIncome(subAccount.getIncome().add(amount));
    subAccount.setUpdatedAt(LocalDateTime.now());
    subAccountRepository.save(subAccount);

    Income income = createIncomeFromSource(source, subAccount);
    createTransactionForIncome(income);
  }

  public void updateSubAccountIncome(SubAccount subAccount, Income income, IncomeStatus status) {

    subAccount.setIncome(subAccount.getIncome().subtract(income.getAmount()));

    if (status.equals(IncomeStatus.ACCEPTED))
      subAccount.setMoney(subAccount.getMoney().add(income.getAmount()));

    subAccountRepository.save(subAccount);

    income.setStatus(status);
    incomeRepository.save(income);
    createTransactionForIncome(income);
  }

  public void updateIncome(String token, String incomeId, UpdateIncomeRequest req) {
    String subAccountId = jwtService.extractSubAccountId(token);
    SubAccountRole subAccountRole = jwtService.extractSubAccountRole(token);
    subAccountRole =
        subAccountRole.equals(SubAccountRole.OWNER) ? SubAccountRole.PARENT : subAccountRole;

    if (subAccountRole.equals(SubAccountRole.CHILD)) throw new NoRight();

    SubAccount subAccount =
        subAccountRepository
            .findById(subAccountId)
            .orElseThrow(() -> new SubAccountNotFoundException(subAccountId));
    Income income =
        incomeRepository.findById(incomeId).orElseThrow(() -> new IncomeNotFound(incomeId));

    updateSubAccountIncome(subAccount, income, req.getStatus());
  }

  public void collectAllIncome(String token, CollectAllIncome req) {
    SubAccountRole role = jwtService.extractSubAccountRole(token);
    String subAccountId = req.getSubAccountId();

    if (role.equals(SubAccountRole.CHILD)) throw new NoRight();

    SubAccount subAccount =
        subAccountRepository
            .findById(subAccountId)
            .orElseThrow(() -> new SubAccountNotFoundException(subAccountId));

    BigDecimal amount = BigDecimal.ZERO.setScale(2);
    List<Income> incomes =
        incomeRepository
            .findAllByStatusAndSubAccountIdChild(IncomeStatus.PENDING, subAccountId)
            .orElseThrow(() -> new IncomeNotFound());

    if (incomes.size() == 0) throw new NoRight();

    for (Income income : incomes) {
      amount = amount.add(income.getAmount());
      updateSubAccountIncome(subAccount, income, IncomeStatus.ACCEPTED);
    }
  }
}
