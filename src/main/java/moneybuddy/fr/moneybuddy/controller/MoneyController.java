/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.Money.AddMoney;
import moneybuddy.fr.moneybuddy.service.MoneyService;
import moneybuddy.fr.moneybuddy.utils.ValidatorResult;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/money")
@RequiredArgsConstructor
public class MoneyController {
  private final ValidatorResult validatorResult;
  private final MoneyService moneyService;

  @PostMapping("")
  public ResponseEntity<AuthResponse> updateMoney(
      @Valid @RequestBody AddMoney request,
      @RequestHeader("Authorization") String authHeader,
      BindingResult bindingResult,
      @RequestParam(required = true) Boolean isAdd) {
    if (bindingResult.hasErrors()) {
      return validatorResult.returnErrorMessage(bindingResult);
    }

    String token = authHeader.substring(7);
    return moneyService.updateMoney(request, token, isAdd);
  }
}
