/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import java.util.List;
import java.util.Optional;

import moneybuddy.fr.moneybuddy.model.TaskHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskHistoryRepository extends MongoRepository<TaskHistory, String> {

  Optional<List<TaskHistory>> findAllByTaskId(String taskId);
}
