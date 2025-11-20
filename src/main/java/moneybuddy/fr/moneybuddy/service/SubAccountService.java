package moneybuddy.fr.moneybuddy.service;

import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.SubAccountDto;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.repository.AccountRepository;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubAccountService {
    private final AccountRepository accountRepository;
    private final SubAccountRepository subAccountRepository;
    private final JwtService jwtService;
    private final DiscordNotificationService discordNotificationService;

    public ResponseEntity<AuthResponse> response(String message, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(AuthResponse.builder()
                    .error(message)
                    .build());
    }

    public ResponseEntity<AuthResponse> addSubAccount(SubAccountDto subAccountDto, String token) {
        SubAccountRole subAccountRole = jwtService.extractSubAccountRole(token);
        String accountId = jwtService.extractSubAccountAccountId(token);

        Optional<Account> optinalaccount = accountRepository.findById(accountId);

        if ( subAccountRole == null ||
            (!SubAccountRole.OWNER.equals(subAccountRole) &&
            !(SubAccountRole.PARENT.equals(subAccountRole) && SubAccountRole.CHILD.equals(subAccountDto.getRole()))))
        {
            return response("Pas autorisé", HttpStatus.UNAUTHORIZED);
        }

        SubAccount subAccount = SubAccount.builder()
                .name(subAccountDto.getName())
                .accountId(accountId)
                .isActive(false)
                .role(subAccountDto.getRole())
                .money("0")
                .createdAt(LocalDateTime.now())
                .build();

        if (SubAccountRole.PARENT.equals(subAccountDto.getRole())) {
            subAccount.setPin(subAccountDto.getPin());
        }

        if (SubAccountRole.CHILD.equals(subAccountDto.getRole())) {
            subAccount.setMoney("0");
        }


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

            // Notification Discord pour la création de sous-compte
            try {
                String parentEmail = jwtService.extractUsername(token);
                discordNotificationService.sendSubAccountCreationNotification(
                    parentEmail,
                    subAccountDto.getName()
                );
            } catch (Exception e) {
                // On log l'erreur mais on ne fait pas échouer la création
                System.err.println("Erreur lors de l'envoi de la notification Discord: " + e.getMessage());
            }

            return response("SubAccount created", HttpStatus.OK);
        }

        return response("Error", HttpStatus.NOT_FOUND);
    }
}
