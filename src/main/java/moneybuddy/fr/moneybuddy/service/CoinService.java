/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.coin.UpdateCoin;
import moneybuddy.fr.moneybuddy.exception.SubAccountNotFoundException;
import moneybuddy.fr.moneybuddy.exception.UpdateCoinError;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.model.enums.TransactionCategory;
import moneybuddy.fr.moneybuddy.model.enums.TransactionType;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import moneybuddy.fr.moneybuddy.repository.TransactionRepository;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoinService {
  private final SubAccountRepository subAccountRepository;
  private final TransactionRepository transactionRepository;
  private final JwtService jwtService;

  public void updateCoin(UpdateCoin req, String token, boolean isAdd) {
    if (req.getCoin() == 0) return;

    String parentId = jwtService.extractSubAccountId(token);
    String accountId = jwtService.extractSubAccountAccountId(token);
    SubAccount subAccount =
        subAccountRepository
            .findById(req.getSubAccountId())
            .orElseThrow(() -> new SubAccountNotFoundException(req.getSubAccountId()));
            
    int coin = subAccount.getCoin();
    int currentBalance = subAccount.getCoin();

    if (isAdd) {
      coin = subAccount.getCoin() + req.getCoin();
    } else {
      if (req.getCoin() > coin) throw new UpdateCoinError(req.getSubAccountId());

      coin = subAccount.getCoin() - req.getCoin();
    }

    subAccount.setCoin(coin);
    subAccountRepository.save(subAccount);

    Transaction transaction =
        Transaction.builder()
            .childId(req.getSubAccountId())
            .parentId(parentId)
            .accountId(accountId)
            .amount(String.valueOf(req.getCoin()))
            .oldAmount(String.valueOf(currentBalance))
            .newAmount(String.valueOf(subAccount.getCoin()))
            .description(req.getDescription())
            .type(isAdd ? TransactionType.CREDIT : TransactionType.DEBIT)
            .category(TransactionCategory.COIN)
            .createdAt(LocalDateTime.now())
            .build();
    transactionRepository.save(transaction);
  }
}
