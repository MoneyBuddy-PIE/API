package moneybuddy.fr.moneybuddy.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import moneybuddy.fr.moneybuddy.model.DepositDetails;

@Repository
public interface GoalTransactionRepository extends MongoRepository<DepositDetails, String> {

    List<DepositDetails> findByGoalId(String goalId);

    Page<DepositDetails> findAllByChildId(String subAccountId, Pageable pageable);
    Page<DepositDetails> findAllByParentId(String subAccountId, Pageable pageable);
    Page<DepositDetails> findAllByAccountId(String accountId, Pageable pageable);
    Page<DepositDetails> findAll(Pageable pageable);
}
