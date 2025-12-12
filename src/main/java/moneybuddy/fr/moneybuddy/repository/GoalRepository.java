/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import java.util.List;

import moneybuddy.fr.moneybuddy.model.Goal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends MongoRepository<Goal, String> {

  List<Goal> findBySubaccountIdChild(String SubaccountIdChild);

  List<Goal> findByAccountId(String accountId);

  Goal findByIdAndSubaccountIdChild(String id, String SubaccountIdChild);

  void deleteByIdAndSubaccountIdChild(String id, String SubaccountIdChild);
}
