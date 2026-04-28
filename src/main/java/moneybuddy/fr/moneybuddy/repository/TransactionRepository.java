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

  @Query(value = "{ 'goalId': { $ne: null }}")
  Page<Transaction> findByChildIdAndGoal(String childId, Pageable pageable);

  @Query(value = "{ 'goalId': { $ne: null }}")
  Page<Transaction> findByAccountIdAndGoal(String accountId, Pageable pageable);

  List<Transaction> findAllByGoalId(String goalId);

  Page<Transaction> findAllByAccountId(String accountId, Pageable pageable);

  Page<Transaction> findAllByAccountIdAndChildId(
      String accountId, String childId, Pageable pageable);

  Page<Transaction> findAllByParentId(String parentId, Pageable pageable);

  Page<Transaction> findAllByChildId(String childId, Pageable pageable);
}
