/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.exception.UpdateCoinError;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.model.enums.TransactionCategory;
import moneybuddy.fr.moneybuddy.model.enums.TransactionType;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoinService {
  private final SubAccountRepository subAccountRepository;
  private final TransactionService transactionService;

  public int updateCoin(SubAccount subAccount, int coin, boolean isAdd) {
    int currentCoin = subAccount.getCoin();

    if (isAdd) {
      currentCoin = subAccount.getCoin() + coin;
    } else {
      if (currentCoin < coin) throw new UpdateCoinError(subAccount.getId());
      currentCoin = subAccount.getCoin() - coin;
    }

    subAccount.setCoin(currentCoin);
    subAccountRepository.save(subAccount);

    return currentCoin;
  }

  public void updateCoinForCourseOrChapter(
      SubAccount subAccount, int coinReward, String description) {
    if (coinReward == 0) return;

    int currentBalance = updateCoin(subAccount, coinReward, true);

    Transaction transaction =
        Transaction.builder()
            .childId(subAccount.getId())
            .accountId(subAccount.getAccountId())
            .amount(String.valueOf(coinReward))
            .oldAmount(String.valueOf(currentBalance))
            .newAmount(String.valueOf(subAccount.getCoin()))
            .description(description)
            .type(TransactionType.CREDIT)
            .category(TransactionCategory.COIN)
            .createdAt(LocalDateTime.now())
            .build();
    transactionService.createTransaction(transaction);
  }

  public void updateCoinForTask(SubAccount subAccount, Task task) {
    if (task.getCoinReward() == 0) return;

    int currentBalance = updateCoin(subAccount, task.getCoinReward(), true);

    Transaction transaction =
        Transaction.builder()
            .childId(task.getSubaccountIdChild())
            .parentId(task.getSubaccountIdParent())
            .accountId(task.getAccountId())
            .amount(String.valueOf(task.getCoinReward()))
            .oldAmount(String.valueOf(currentBalance))
            .newAmount(String.valueOf(subAccount.getCoin()))
            .description(task.getDescription())
            .type(TransactionType.CREDIT)
            .category(TransactionCategory.COIN)
            .createdAt(LocalDateTime.now())
            .build();
    transactionService.createTransaction(transaction);
  }
}
