/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.exception.CustomError;
import moneybuddy.fr.moneybuddy.exception.RefreshTokenExpired;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.RefreshToken;
import moneybuddy.fr.moneybuddy.repository.RefreshTokenRepository;
import moneybuddy.fr.moneybuddy.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final Utils utils;
  private final RefreshTokenRepository refreshTokenRepository;
  private final AccountService accountService;
  private final JwtService jwtService;

  public String createRefreshToken(String accountId) {
    String token = utils.generateKey(accountId);

    RefreshToken refreshToken =
        RefreshToken.builder().accountId(accountId).refreshToken(token).build();

    refreshTokenRepository.save(refreshToken);
    return token;
  }

  public void deleteRefreshToken(String token, String refreshToken) {
    String accoundId = jwtService.extractSubAccountAccountId(token);
    refreshTokenRepository.deleteByRefreshTokenAndAccountId(refreshToken, accoundId);
  }

  public AuthResponse refreshToken(String refreshToken) {
    RefreshToken newRefreshToken =
        refreshTokenRepository
            .findByRefreshToken(refreshToken)
            .orElseThrow(
                () ->
                    new CustomError(
                        "Refresh token not found", HttpStatus.CONFLICT, "REFRESH_TOKEN_ERROR"));

    if (newRefreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(newRefreshToken);
      throw new RefreshTokenExpired();
    }

    Account account = accountService.getAccount(newRefreshToken.getAccountId());
    String token = jwtService.generateToken(account, account.getId(), account.getRole());

    return AuthResponse.builder().token(token).build();
  }
}
