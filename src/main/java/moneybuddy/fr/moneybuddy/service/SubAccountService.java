/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.SubAccountDto;
import moneybuddy.fr.moneybuddy.exception.SubAccountNotFoundException;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.repository.AccountRepository;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubAccountService {

  private final AccountRepository accountRepository;
  private final SubAccountRepository subAccountRepository;
  private final SettingService settingService;
  private final JwtService jwtService;
  private final DiscordService discordService;

  public ResponseEntity<AuthResponse> response(String message, HttpStatus status) {
    return ResponseEntity.status(status).body(AuthResponse.builder().error(message).build());
  }

  public ResponseEntity<AuthResponse> addSubAccount(SubAccountDto subAccountDto, String token) {
    SubAccountRole subAccountRole = jwtService.extractSubAccountRole(token);
    String accountId = jwtService.extractSubAccountAccountId(token);

    Optional<Account> optinalaccount = accountRepository.findById(accountId);

    if (subAccountRole == null
        || (!SubAccountRole.OWNER.equals(subAccountRole)
            && !(SubAccountRole.PARENT.equals(subAccountRole)
                && SubAccountRole.CHILD.equals(subAccountDto.getRole())))) {
      return response("Pas autorisé", HttpStatus.UNAUTHORIZED);
    }

    SubAccount subAccount =
        SubAccount.builder()
            .name(subAccountDto.getName())
            .accountId(accountId)
            .isActive(false)
            .role(subAccountDto.getRole())
            .createdAt(LocalDateTime.now())
            .build();

    if (subAccountDto.getIconName() != null && subAccountDto.getIconStyle() != null) {
      subAccount.setIconName(subAccountDto.getIconName());
      subAccount.setIconStyle(subAccountDto.getIconStyle());
    }

    if (SubAccountRole.PARENT.equals(subAccountDto.getRole()))
      subAccount.setPin(subAccountDto.getPin());

    if (optinalaccount.isPresent()) {
      subAccountRepository.save(subAccount);

      Account account = optinalaccount.get();
      List<SubAccount> subAccounts = account.getSubAccounts();

      if (subAccounts == null) {
        subAccounts = new ArrayList<>();
      }

      subAccounts.add(subAccount);
      account.setSubAccounts(subAccounts);
      accountRepository.save(account);
      subAccount.setSetting(settingService.createSetting(subAccount));
      subAccountRepository.save(subAccount);

      discordService.sendNewAccountMessage(account.getEmail(), subAccount, false);

      return response("SubAccount created", HttpStatus.OK);
    }

    return response("Error", HttpStatus.NOT_FOUND);
  }

  public SubAccount get(String id) {
    return subAccountRepository.findById(id).orElseThrow(() -> new SubAccountNotFoundException(id));
  }
}
