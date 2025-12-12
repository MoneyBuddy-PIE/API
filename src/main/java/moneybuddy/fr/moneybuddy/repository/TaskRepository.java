/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import java.util.List;

import moneybuddy.fr.moneybuddy.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

  List<Task> findBySubaccountIdChild(String SubaccountIdChild);

  List<Task> findBySubaccountIdParent(String subaccountIdParent);

  List<Task> findByAccountId(String accountId);

  Task findByIdAndSubaccountIdParent(String id, String subAccountId);
}
