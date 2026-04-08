/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import java.util.Optional;

import moneybuddy.fr.moneybuddy.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
  Optional<RefreshToken> findByRefreshToken(String refreshToken);

  void deleteByRefreshTokenAndAccountId(String refreshToken, String accountId);
}
