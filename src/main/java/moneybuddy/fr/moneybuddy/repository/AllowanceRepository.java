/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import moneybuddy.fr.moneybuddy.model.Allowance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllowanceRepository extends MongoRepository<Allowance, String> {

  Optional<List<Allowance>> findAllByAccountId(String accountId);

  Optional<List<Allowance>> findByActiveTrueAndNextExecutionIsNullOrNextExecutionEquals(
      LocalDate date);
}
