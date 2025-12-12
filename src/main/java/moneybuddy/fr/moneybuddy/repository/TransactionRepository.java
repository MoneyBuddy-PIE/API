/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import java.util.List;

import moneybuddy.fr.moneybuddy.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

  List<Transaction> findAllByChildId(String childId);

  @Query(value = "{ 'goalId': { $ne: null }}")
  List<Transaction> findByChildAndGoal(String childId);

  List<Transaction> findAllByGoalId(String goalId);

  Page<Transaction> findAllByAccountId(String accountId, Pageable pageable);

  Page<Transaction> findAllByChildId(Pageable pageable, String childId);
}
