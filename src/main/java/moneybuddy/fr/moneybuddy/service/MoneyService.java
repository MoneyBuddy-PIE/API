/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.Money.AddMoney;
import moneybuddy.fr.moneybuddy.exception.NoRight;
import moneybuddy.fr.moneybuddy.exception.UpdateMoneyError;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.enums.TransactionCategory;
import moneybuddy.fr.moneybuddy.model.enums.TransactionType;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import moneybuddy.fr.moneybuddy.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoneyService {
  private final SubAccountRepository subAccountRepository;
  private final TransactionRepository transactionRepository;
  private final JwtService jwtService;

  public void updateMoney(AddMoney request, String token, boolean isAdd) {
    SubAccount subAccount =
        subAccountRepository
            .findById(request.getSubAccountId())
            .orElseThrow(() -> new IllegalArgumentException("SubAccount non trouvé"));

    if (SubAccountRole.CHILD.equals(jwtService.extractSubAccountRole(token))
        && isAdd
        && !request.getSubAccountId().equals(subAccount.getId())) throw new NoRight();

    String parentId = jwtService.extractSubAccountId(token);
    String accountId = jwtService.extractSubAccountAccountId(token);

    BigDecimal amount;
    try {
      amount = request.getAmount();
    } catch (NumberFormatException e) {
      throw new UpdateMoneyError(accountId, "Montant invalide");
    }

    BigDecimal currentBalance =
        subAccount.getMoney() == null ? BigDecimal.ZERO.setScale(2) : subAccount.getMoney();

    if (!isAdd && currentBalance.compareTo(amount) < 0) {
      throw new UpdateMoneyError(accountId, "Fonds insuffisant");
    }

    BigDecimal newBalance = isAdd ? currentBalance.add(amount) : currentBalance.subtract(amount);

    subAccount.setMoney(newBalance);
    subAccountRepository.save(subAccount);

    Transaction transaction =
        Transaction.builder()
            .childId(request.getSubAccountId())
            .parentId(parentId)
            .accountId(accountId)
            .amount(String.valueOf(amount))
            .oldAmount(String.valueOf(currentBalance))
            .newAmount(String.valueOf(newBalance))
            .description(request.getDescription())
            .type(isAdd ? TransactionType.CREDIT : TransactionType.DEBIT)
            .category(TransactionCategory.MONEY)
            .createdAt(LocalDateTime.now())
            .build();
    transactionRepository.save(transaction);
  }
}
