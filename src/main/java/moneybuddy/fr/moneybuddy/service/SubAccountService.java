/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.SubAccountDto;
import moneybuddy.fr.moneybuddy.dtos.subAccount.UpdateSubAccountDto;
import moneybuddy.fr.moneybuddy.exception.AccountNotFoundException;
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
  private final UserProgressService userProgressService;
  private final JwtService jwtService;
  private final DiscordService discordService;

  public ResponseEntity<AuthResponse> response(String message, HttpStatus status) {
    return ResponseEntity.status(status).body(AuthResponse.builder().error(message).build());
  }

  public SubAccount getById(String id) {
    return subAccountRepository.findById(id).orElseThrow(() -> new SubAccountNotFoundException(id));
  }

  public ResponseEntity<AuthResponse> addSubAccount(SubAccountDto subAccountDto, String token) {
    SubAccountRole subAccountRole = jwtService.extractSubAccountRole(token);
    String accountId = jwtService.extractSubAccountAccountId(token);
    Account account =
        accountRepository
            .findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException(accountId));

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
            .pin(subAccountDto.getPin())
            .createdAt(LocalDateTime.now())
            .build();

    if (subAccountDto.getIconName() != null && subAccountDto.getIconStyle() != null) {
      subAccount.setIconName(subAccountDto.getIconName());
      subAccount.setIconStyle(subAccountDto.getIconStyle());
    }

    subAccount.setSetting(settingService.createSetting(subAccount));
    subAccountRepository.save(subAccount);

    account.getSubAccounts().put(subAccount.getId(), subAccount);
    accountRepository.save(account);

    userProgressService.createBasicUserProgress(subAccount);

    discordService.sendNewAccountMessage(account.getEmail(), subAccount, false);

    return response("SubAccount created", HttpStatus.OK);
  }

  public SubAccount updateSubAccount(String token, UpdateSubAccountDto req) {
    String subAccountId = jwtService.extractSubAccountId(token);
    SubAccount subAccount = getById(subAccountId);

    subAccount.setName(Optional.ofNullable(req.getName()).orElse(subAccount.getName()));
    subAccount.setIconName(Optional.ofNullable(req.getIconName()).orElse(subAccount.getIconName()));
    subAccount.setIconStyle(
        Optional.ofNullable(req.getIconStyle()).orElse(subAccount.getIconStyle()));

    return subAccountRepository.save(subAccount);
  }
}
