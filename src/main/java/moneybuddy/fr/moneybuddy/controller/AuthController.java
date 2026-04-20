/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthRequest;
import moneybuddy.fr.moneybuddy.dtos.AuthResetPassword;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.AuthSubAccountRequest;
import moneybuddy.fr.moneybuddy.dtos.RegisterRequest;
import moneybuddy.fr.moneybuddy.dtos.ResponseDto;
import moneybuddy.fr.moneybuddy.dtos.device.CreateDeviceRequest;
import moneybuddy.fr.moneybuddy.dtos.refreshToken.RefreshTokenRequest;
import moneybuddy.fr.moneybuddy.dtos.subAccount.UpdateSubAccountDto;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.service.AuthService;
import moneybuddy.fr.moneybuddy.service.DeviceService;
import moneybuddy.fr.moneybuddy.service.RefreshTokenService;
import moneybuddy.fr.moneybuddy.service.SubAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService service;
  private final DeviceService deviceService;
  private final SubAccountService subAccountService;
  private final RefreshTokenService refreshTokenService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.authenticate(request));
  }

  @PostMapping("/logout")
  public ResponseEntity<ResponseDto> deleteRefreshToken(
      @RequestHeader("Authorization") String authHeader,
      @Valid @RequestBody RefreshTokenRequest req) {
    String token = authHeader.substring(7);
    refreshTokenService.deleteRefreshToken(token, req.getRefreshToken());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDto.builder().message("Disconnected with success !").build());
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest req) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(refreshTokenService.refreshToken(req.getRefreshToken()));
  }

  @GetMapping("/me")
  public ResponseEntity<Account> getMe(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    return service.getMe(token);
  }

  @PostMapping("/subAccount/login")
  public ResponseEntity<AuthResponse> authenticateSubAccount(
      @Valid @RequestBody AuthSubAccountRequest request,
      @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    return service.authenticateSubAccount(request, token);
  }

  @GetMapping("/subAccount/me")
  public ResponseEntity<SubAccount> getSubAccountMe(
      @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    return service.getSubAccountMe(token);
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody AuthRequest request) {
    return service.resetPassword(request);
  }

  @PostMapping("/reset-password/confirm")
  public ResponseEntity<AuthResponse> resetPasswordConfirm(
      @RequestBody AuthResetPassword request, @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    return service.restPasswordConfirm(request, token);
  }

  @PostMapping("/device")
  public ResponseEntity<ResponseDto> createDevice(
      @RequestHeader("Authorization") String authHeader,
      @Valid @RequestBody CreateDeviceRequest request) {
    String token = authHeader.substring(7);
    deviceService.createOrUpdateDevice(token, request);

    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(ResponseDto.builder().message("Appareil enregistré.").build());
  }

  @PutMapping("/device/desactivate")
  public ResponseEntity<ResponseDto> desactivateDevice(
      @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    deviceService.desactivateDevice(token);

    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(ResponseDto.builder().message("Appareil désactivé.").build());
  }

  @PutMapping("/subAccount")
  public ResponseEntity<SubAccount> updateSubAccount(
      @RequestHeader("Authorization") String authHeader,
      @Valid @RequestBody UpdateSubAccountDto req) {
    String token = authHeader.substring(7);
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(subAccountService.updateSubAccount(token, req));
  }
}
