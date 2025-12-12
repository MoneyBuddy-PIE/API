/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.utils;

import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class ValidatorResult {

  public ResponseEntity<AuthResponse> returnErrorMessage(BindingResult bindingResult) {
    String errorMessage = bindingResult.getFieldError().getDefaultMessage();
    return ResponseEntity.badRequest().body(AuthResponse.builder().error(errorMessage).build());
  }
}
