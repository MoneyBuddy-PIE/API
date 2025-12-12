package moneybuddy.fr.moneybuddy.service;

import moneybuddy.fr.moneybuddy.dtos.AuthRequest;
import moneybuddy.fr.moneybuddy.dtos.AuthResetPassword;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.AuthSubAccountRequest;
import moneybuddy.fr.moneybuddy.dtos.RegisterRequest;
import moneybuddy.fr.moneybuddy.exception.AccountNotFoundException;
import moneybuddy.fr.moneybuddy.exception.EmailAlreadyExistsException;
import moneybuddy.fr.moneybuddy.exception.InvalidCredentialsException;
import moneybuddy.fr.moneybuddy.exception.InvalidPinException;
import moneybuddy.fr.moneybuddy.exception.PasswordMismatchException;
import moneybuddy.fr.moneybuddy.exception.SubAccountNotFoundException;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.enums.Role;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.enums.PlanType;
import moneybuddy.fr.moneybuddy.repository.AccountRepository;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import moneybuddy.fr.moneybuddy.utils.EmailService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountRepository repository;
    private final SubAccountRepository subAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public ResponseEntity<AuthResponse> register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        if (repository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Account account = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .subscriptionStatus(false)
                .planType(PlanType.FREE)
                .build();
        repository.save(account);

        SubAccount subAccount = SubAccount.builder()
                                .name(request.getName())
                                .pin(request.getPin())
                                .accountId(account.getId())
                                .role(SubAccountRole.OWNER)
                                .isActive(true)
                                .createdAt(LocalDateTime.now())
                                .build();

        subAccountRepository.save(subAccount);

        List<SubAccount> subAccounts = Arrays.asList(subAccount);
        account.setSubAccounts(subAccounts);
        repository.save(account);
        
        String jwtToken = jwtService.generateToken(account, account.getRole());

        emailService.welcomeEmail(account.getEmail());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(AuthResponse.builder()
                .token(jwtToken)
                .build());
    }

    public ResponseEntity<AuthResponse> authenticate(AuthRequest request) {
        Account account = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AccountNotFoundException(request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String jwtToken = jwtService.generateToken(account, account.getRole());
        account.setLastConnexion(LocalDateTime.now());
        repository.save(account);

        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(AuthResponse.builder()
                .token(jwtToken)
                .build());
    }

    public ResponseEntity<Account> getMe(String token) {
        String email = jwtService.extractUsername(token);
        Account user = repository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException(email));

        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(user);
    }

    public ResponseEntity<AuthResponse> authenticateSubAccount(AuthSubAccountRequest request, String token) {
        String subAccountId = request.getId();
        String pin = request.getPin();
        String email = jwtService.extractUsername(token);

        Role role = jwtService.extractAccountRole(token);
        SubAccount subAccount = subAccountRepository.findById(subAccountId)
                .orElseThrow(() -> new SubAccountNotFoundException(subAccountId));

        if (!SubAccountRole.CHILD.equals(subAccount.getRole()) && !pin.equals(subAccount.getPin())) {
            throw new InvalidPinException();
        }

        if (SubAccountRole.PARENT.equals(subAccount.getRole()) && (request.getPin() == null || request.getPin().isEmpty())) {
            throw new InvalidPinException("Le code PIN est obligatoire pour un compte parent");
        }

        var jwtToken = jwtService.generateSubAccountToken(subAccountId, subAccount.getAccountId(), email, subAccount.getRole(), role);
        subAccount.setLastConnexion(LocalDateTime.now());
        subAccountRepository.save(subAccount);

        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(AuthResponse.builder()
                .token(jwtToken)
                .build());
    }

    public ResponseEntity<SubAccount> getSubAccountMe(String token) {
        String subAccountId = jwtService.extractSubAccountId(token);
        SubAccount subAccount = subAccountRepository.findById(subAccountId)
                .orElseThrow(() -> new SubAccountNotFoundException(subAccountId));

        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(subAccount);
    }

    public ResponseEntity<Void> resetPassword(AuthRequest request) {
        Account account = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AccountNotFoundException(request.getEmail()));

        String token = jwtService.generateToken(account, account.getRole());

        emailService.resetPasswordEmail(request.getEmail(), token);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    } 

    public ResponseEntity<AuthResponse> restPasswordConfirm(AuthResetPassword request, String token) {
        String accountId = jwtService.extractUsername(token);
        Account account = repository.findByEmail(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        SubAccount subAccount = subAccountRepository.findByAccountIdAndRole(account.getId(), SubAccountRole.OWNER);

        if (subAccount == null) {
            throw new SubAccountNotFoundException("Compte propriétaire introuvable");
        }

        if (!subAccount.getPin().equals(request.getPin())) {
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