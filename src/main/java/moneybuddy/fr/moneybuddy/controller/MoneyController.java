package moneybuddy.fr.moneybuddy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.Money.AddMoney;
import moneybuddy.fr.moneybuddy.service.MoneyService;
import moneybuddy.fr.moneybuddy.utils.ValidatorResult;

@RestController
@RequestMapping("/money")
@RequiredArgsConstructor
public class MoneyController {
    private final ValidatorResult validatorResult;
    private final MoneyService moneyService;

    @PostMapping("")
    public ResponseEntity<AuthResponse> addMoney(
        @Valid @RequestBody AddMoney request,
        @RequestHeader("Authorization") String authHeader,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return validatorResult.returnErrorMessage(bindingResult);
        }

        String token = authHeader.substring(7);
        return moneyService.updateMoney(request, token, true);
    }

    @PostMapping("/remove")
    public ResponseEntity<AuthResponse> removeMoney(
        @Valid @RequestBody AddMoney request,
        @RequestHeader("Authorization") String authHeader,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return validatorResult.returnErrorMessage(bindingResult);
        }

        String token = authHeader.substring(7);
        return moneyService.updateMoney(request, token, false);
    }

    @GetMapping("/balance/{subAccountId}")
    public ResponseEntity<?> getBalance(@PathVariable String subAccountId) {
        return moneyService.getSubAccountBalance(subAccountId);
    }

    @GetMapping("/transactions/{subAccountId}")
    public ResponseEntity<?> getTransactions(@PathVariable String subAccountId) {
        return moneyService.getAllTransactions(subAccountId);
    }
}
