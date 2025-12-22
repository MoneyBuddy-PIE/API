/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import java.util.List;
import java.util.Optional;

import moneybuddy.fr.moneybuddy.model.Income;
import moneybuddy.fr.moneybuddy.model.enums.IncomeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeRepository extends MongoRepository<Income, String> {

  Optional<List<Income>> findAllBySubAccountIdChild(String id);

  Optional<List<Income>> findAllBySubAccountId(String subAccountId);

  Optional<List<Income>> findAllByStatusAndSubAccountIdChild(
      IncomeStatus status, String subAccountIdChild);
}
