package moneybuddy.fr.moneybuddy.service;

import moneybuddy.fr.moneybuddy.dtos.AuthRequest;
import moneybuddy.fr.moneybuddy.dtos.AuthResetPassword;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.AuthSubAccountRequest;
import moneybuddy.fr.moneybuddy.dtos.RegisterRequest;
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

    public ResponseEntity<AuthResponse> response(String message, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(AuthResponse.builder()
                    .error(message)
                    .build());
    }

    public ResponseEntity<AuthResponse> register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return response("Not same password" ,HttpStatus.BAD_REQUEST);
        }
        
        if (repository.findByEmail(request.getEmail()).isPresent()){
            return response("L'email est déjà utilisé" ,HttpStatus.BAD_REQUEST);
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
        
        String jwtToken = jwtService.generateToken(account);

        emailService.welcomeEmail(account.getEmail());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(AuthResponse.builder()
                .token(jwtToken)
                .build());
    }

    public ResponseEntity<AuthResponse> authenticate(AuthRequest request) {   
        Account account = repository.findByEmail(request.getEmail()).orElseThrow();

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            return response("Mot de passe incorrect", HttpStatus.UNAUTHORIZED);
        }

        String jwtToken = jwtService.generateToken(account);
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
        Account user = repository.findByEmail(email).orElseThrow();

        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(user);
    }

    public ResponseEntity<AuthResponse> authenticateSubAccount(AuthSubAccountRequest request, String token) {
        String subAccountId = request.getId();
        String pin = request.getPin();
        String email = jwtService.extractUsername(token);

        SubAccount subAccount = subAccountRepository.findById(subAccountId).orElseThrow();

        if (!SubAccountRole.CHILD.equals(subAccount.getRole()) && !pin.equals(subAccount.getPin())) {
            return response("Mauvais pin pour le compte parent", HttpStatus.BAD_REQUEST);
        }        

        var jwtToken = jwtService.generateSubAccountToken(subAccountId, subAccount.getAccountId(), email, subAccount.getRole());
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
        SubAccount subAccount = subAccountRepository.findById(subAccountId).orElseThrow();

        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(subAccount);
    }

    public ResponseEntity<Void> resetPassword(AuthRequest request) {
        Account account = repository.findByEmail(request.getEmail()).orElseThrow();

        String token = jwtService.generateToken(account);

        emailService.resetPasswordEmail(request.getEmail(), token);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    } 

    public ResponseEntity<AuthResponse> restPasswordConfirm(AuthResetPassword request, String token) {
        String accountId = jwtService.extractUsername(token);
        Account account = repository.findByEmail(accountId).orElseThrow();

        SubAccount subAccount = subAccountRepository.findByAccountIdAndRole(account.getId(), SubAccountRole.OWNER);

        if (!subAccount.getPin().equals(request.getPin())) {
            return response("UNAUTHORIZED not same pin as the ower", HttpStatus.UNAUTHORIZED);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return response("Not same password", HttpStatus.BAD_REQUEST);
        }

        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setUpdatedAt(LocalDateTime.now());
        repository.save(account);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}