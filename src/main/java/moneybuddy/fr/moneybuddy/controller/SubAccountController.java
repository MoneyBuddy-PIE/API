package moneybuddy.fr.moneybuddy.controller;

import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.SubAccountDto;
import moneybuddy.fr.moneybuddy.service.SubAccountService;
import moneybuddy.fr.moneybuddy.utils.ValidatorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/sub-accounts")
@RequiredArgsConstructor
public class SubAccountController {
    private final SubAccountService subAccountService;
     private final ValidatorResult validatorResult;

    @PostMapping("")
    public ResponseEntity<AuthResponse> addSubAccount(
            @Valid @RequestBody SubAccountDto subAccount,
            @RequestHeader("Authorization") String authHeader,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return validatorResult.returnErrorMessage(bindingResult);
        }

        String token = authHeader.substring(7);
        return subAccountService.addSubAccount(subAccount, token);
    }
}