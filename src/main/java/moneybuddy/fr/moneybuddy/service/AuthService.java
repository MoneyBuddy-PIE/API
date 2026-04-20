/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthRequest;
import moneybuddy.fr.moneybuddy.dtos.AuthResetPassword;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.AuthSubAccountRequest;
import moneybuddy.fr.moneybuddy.dtos.RegisterRequest;
import moneybuddy.fr.moneybuddy.exception.AccountDesactivated;
import moneybuddy.fr.moneybuddy.exception.AccountNotFoundException;
import moneybuddy.fr.moneybuddy.exception.EmailAlreadyExistsException;
import moneybuddy.fr.moneybuddy.exception.InvalidCredentialsException;
import moneybuddy.fr.moneybuddy.exception.InvalidPinException;
import moneybuddy.fr.moneybuddy.exception.PasswordMismatchException;
import moneybuddy.fr.moneybuddy.exception.SubAccountDesactivated;
import moneybuddy.fr.moneybuddy.exception.SubAccountNotFoundException;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.enums.PlanType;
import moneybuddy.fr.moneybuddy.model.enums.Role;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.repository.AccountRepository;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import moneybuddy.fr.moneybuddy.utils.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AccountRepository repository;
  private final SubAccountRepository subAccountRepository;
  private final SettingService settingService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final EmailService emailService;
  private final DiscordService discordService;
  private final UserProgressService userProgressService;
  private final RefreshTokenService refreshTokenService;

  public AuthResponse register(RegisterRequest request) {
    if (!request.getPassword().equals(request.getConfirmPassword())) {
      throw new PasswordMismatchException();
    }

    if (repository.findByEmail(request.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException(request.getEmail());
    }

    Account account =
        Account.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .createdAt(LocalDateTime.now())
            .subscriptionStatus(false)
            .planType(PlanType.FREE)
            .build();
    repository.save(account);

    SubAccount subAccount =
        SubAccount.builder()
            .name(request.getName())
            .pin(passwordEncoder.encode(request.getPin()))
            .accountId(account.getId())
            .role(SubAccountRole.OWNER)
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    subAccountRepository.save(subAccount);

    account.getSubAccounts().put(subAccount.getId(), subAccount);
    repository.save(account);

    subAccount.setSetting(settingService.createSetting(subAccount));
    subAccountRepository.save(subAccount);

    userProgressService.createBasicUserProgress(subAccount);

    String jwtToken = jwtService.generateToken(account, account.getId(), account.getRole());
    String refreshToken = refreshTokenService.createRefreshToken(account.getId());

    emailService.welcomeEmail(account.getEmail());
    discordService.sendNewAccountMessage(account.getEmail(), subAccount, true);

    return AuthResponse.builder().token(jwtToken).refreshToken(refreshToken).build();
  }

  public AuthResponse authenticate(AuthRequest request) {
    Account account =
        repository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new AccountNotFoundException(request.getEmail()));

    if (!account.isActivated()) throw new AccountDesactivated(account.getId());

    if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
      throw new InvalidCredentialsException();
    }

    String jwtToken = jwtService.generateToken(account, account.getId(), account.getRole());
    account.setLastConnexion(LocalDateTime.now());
    repository.save(account);

    String refreshToken = refreshTokenService.createRefreshToken(account.getId());
    return AuthResponse.builder().token(jwtToken).refreshToken(refreshToken).build();
  }

  public ResponseEntity<Account> getMe(String token) {
    String email = jwtService.extractUsername(token);
    Account user =
        repository.findByEmail(email).orElseThrow(() -> new AccountNotFoundException(email));

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
  }

  public ResponseEntity<AuthResponse> authenticateSubAccount(
      AuthSubAccountRequest request, String token) {
    String subAccountId = request.getId();
    String pin = request.getPin();
    String email = jwtService.extractUsername(token);

    Role role = jwtService.extractAccountRole(token);
    SubAccount subAccount =
        subAccountRepository
            .findById(subAccountId)
            .orElseThrow(() -> new SubAccountNotFoundException(subAccountId));

    if (!subAccount.isActive()) throw new SubAccountDesactivated(subAccount.getId());

    // Vérification du PIN null/vide en premier pour PARENT et OWNER avant toute comparaison
    if (!SubAccountRole.CHILD.equals(subAccount.getRole())
        && (pin == null || pin.isEmpty())) {
      throw new InvalidPinException("Le code PIN est obligatoire pour ce type de compte");
    }

    // Vérification du PIN hashé pour les comptes non-enfant
    if (!SubAccountRole.CHILD.equals(subAccount.getRole())
        && !passwordEncoder.matches(pin, subAccount.getPin())) {
      throw new InvalidPinException();
    }

    var jwtToken =
        jwtService.generateSubAccountToken(
            subAccountId, subAccount.getAccountId(), email, subAccount.getRole(), role);
    subAccount.setLastConnexion(LocalDateTime.now());
    subAccountRepository.save(subAccount);

    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(AuthResponse.builder().token(jwtToken).build());
  }

  public ResponseEntity<SubAccount> getSubAccountMe(String token) {
    String subAccountId = jwtService.extractSubAccountId(token);
    SubAccount subAccount =
        subAccountRepository
            .findById(subAccountId)
            .orElseThrow(() -> new SubAccountNotFoundException(subAccountId));

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(subAccount);
  }

  public ResponseEntity<Void> resetPassword(AuthRequest request) {
    Account account =
        repository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new AccountNotFoundException(request.getEmail()));

    String token = jwtService.generateToken(account, account.getId(), account.getRole());

    emailService.resetPasswordEmail(request.getEmail(), token);

    return ResponseEntity.status(HttpStatus.OK).body(null);
  }

  public ResponseEntity<AuthResponse> resetPasswordConfirm(AuthResetPassword request, String token) {
    String accountId = jwtService.extractUsername(token);
    Account account =
        repository
            .findByEmail(accountId)
            .orElseThrow(() -> new AccountNotFoundException(accountId));

    SubAccount subAccount =
        subAccountRepository.findByAccountIdAndRole(account.getId(), SubAccountRole.OWNER);

    if (subAccount == null) {
      throw new SubAccountNotFoundException("Compte propriétaire introuvable");
    }

    if (!passwordEncoder.matches(request.getPin(), subAccount.getPin())) {
      throw new InvalidPinException();
    }

    if (!request.getPassword().equals(request.getConfirmPassword())) {
      throw new PasswordMismatchException();
    }

    account.setPassword(passwordEncoder.encode(request.getPassword()));
    account.setUpdatedAt(LocalDateTime.now());
    repository.save(account);
    return ResponseEntity.status(HttpStatus.OK).body(null);
  }
}
