package moneybuddy.fr.moneybuddy.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import moneybuddy.fr.moneybuddy.dtos.AuthResponse;

@Component
public class ValidatorResult {
    
    public ResponseEntity<AuthResponse> returnErrorMessage( 
        BindingResult bindingResult
    ) {
        String errorMessage = bindingResult.getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest().body(AuthResponse.builder()
            .error(errorMessage)
            .build());
    }
}
