/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.model.enums.TaskStatus;
import moneybuddy.fr.moneybuddy.model.enums.TaskType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

  List<Task> findBySubaccountIdChild(String subaccountIdChild);

  List<Task> findBySubaccountIdParent(String subaccountIdParent);

  List<Task> findByAccountId(String accountId);

  Optional<Task> findByIdAndSubaccountIdParent(String id, String subAccountId);

  @Query(
      value =
          "{ 'status': ?0, 'type': { $in: ?1 }, $or: [ {'dateLimit': { $lt: ?2 }}, { 'dateLimit': null } ] }")
  List<Task> findCompletedTasks(TaskStatus status, List<TaskType> types, LocalDateTime now);
}
