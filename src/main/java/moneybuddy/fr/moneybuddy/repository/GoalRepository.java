package moneybuddy.fr.moneybuddy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import moneybuddy.fr.moneybuddy.model.Goal;

@Repository
public interface GoalRepository extends MongoRepository<Goal, String> {

    List<Goal> findBySubaccountIdChild(String SubaccountIdChild);
    List<Goal> findBySubaccountIdParent(String subaccountIdParent);
    List<Goal> findByAccountId(String accountId);

    Goal findByIdAndSubaccountIdParent(String id, String subaccountIdParent);
    Goal findByIdAndSubaccountIdChild(String id, String SubaccountIdChild);
}
