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
import moneybuddy.fr.moneybuddy.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoinService {
  private final SubAccountRepository subAccountRepository;
  private final TransactionRepository transactionRepository;

  public void updateCoin(SubAccount subAccount, Task task, boolean isAdd) {
    if (task.getCoinReward() == 0) return;

    int coin = subAccount.getCoin();
    int currentBalance = subAccount.getCoin();

    if (isAdd) {
      coin = subAccount.getCoin() + task.getCoinReward();
    } else {
      if (task.getCoinReward() > coin) throw new UpdateCoinError(task.getSubaccountIdChild());

      coin = subAccount.getCoin() - task.getCoinReward();
    }

    subAccount.setCoin(coin);
    subAccountRepository.save(subAccount);

    Transaction transaction =
        Transaction.builder()
            .childId(task.getSubaccountIdChild())
            .parentId(task.getSubaccountIdParent())
            .accountId(task.getAccountId())
            .amount(String.valueOf(task.getCoinReward()))
            .oldAmount(String.valueOf(currentBalance))
            .newAmount(String.valueOf(subAccount.getCoin()))
            .description(task.getDescription())
            .type(isAdd ? TransactionType.CREDIT : TransactionType.DEBIT)
            .category(TransactionCategory.COIN)
            .createdAt(LocalDateTime.now())
            .build();
    transactionRepository.save(transaction);
  }
}
