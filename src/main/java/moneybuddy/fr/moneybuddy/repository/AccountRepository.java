/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import java.util.List;
import java.util.Optional;

import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.enums.PlanType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AccountRepository extends MongoRepository<Account, String> {
  @Query(value = "{ 'email': ?0, 'activated': true }")
  Optional<Account> findByEmail(String email);

  @Query(value = "{ 'planType': ?0 }")
  Page<Account> findAllByPlanType(PlanType planType, Pageable pageable);

  Page<Account> findAll(Pageable pageable);

  List<Account> findAll();
}
