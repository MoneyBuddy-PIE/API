package moneybuddy.fr.moneybuddy.controller;

import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.SubAccountDto;
import moneybuddy.fr.moneybuddy.service.SubAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sub-accounts")
@RequiredArgsConstructor
public class SubAccountController {
    private final SubAccountService subAccountService;

    @PostMapping
    public ResponseEntity<AuthResponse> addSubAccount(
            @RequestBody SubAccountDto subAccount,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        return subAccountService.addSubAccount(subAccount, token);
    }
}