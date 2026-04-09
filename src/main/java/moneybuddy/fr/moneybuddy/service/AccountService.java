/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.ResponseDto;
import moneybuddy.fr.moneybuddy.dtos.account.UpdateAccountForAdmin;
import moneybuddy.fr.moneybuddy.exception.AccountNotFoundException;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.enums.PlanType;
import moneybuddy.fr.moneybuddy.model.enums.Role;
import moneybuddy.fr.moneybuddy.repository.AccountRepository;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import moneybuddy.fr.moneybuddy.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final SubAccountRepository subAccountRepository;
  private final Utils utils;

  public Page<Account> getAccounts(
      PlanType planType, int page, int size, String sortBy, String sortDir) {
    Pageable pageable = utils.pagination(page, size, sortBy, sortDir);

    Page<Account> accounts =
        (planType != null)
            ? accountRepository.findAllByPlanType(planType, pageable)
            : accountRepository.findAll(pageable);

    return accounts;
  }

  public Account getAccount(String id) {
    return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
  }

  public ResponseEntity<ResponseDto> deleteAccount(String id) {
    accountRepository.deleteById(id);
    subAccountRepository.deleteAllByAccountId(id);
    return ResponseEntity.status(204).body(null);
  }

  public ResponseDto changeAccountStatus(String id) {
    Account account = accountRepository.findById(id).orElseThrow();

    if (Role.ADMIN.equals(account.getRole())) {
      return ResponseDto.builder().message("No right").status(HttpStatus.FORBIDDEN).build();
    }

    account.setActivated(!account.isActivated());
    String message = "Status Changed to " + (account.isActivated() ? "activated" : "desabled");
    accountRepository.save(account);

    return ResponseDto.builder().message(message).status(HttpStatus.OK).build();
  }

  public Account updateAccount(String id, UpdateAccountForAdmin req) {
    Account account = getAccount(id);

    account.setPlanType(Optional.ofNullable(req.getPlanType()).orElse(account.getPlanType()));
    account.setRole(Optional.ofNullable(req.getRole()).orElse(account.getRole()));
    account.setSubscriptionStatus(
        Optional.ofNullable(req.isSubscriptionStatus()).orElse(account.isSubscriptionStatus()));

    return accountRepository.save(account);
  }
}
