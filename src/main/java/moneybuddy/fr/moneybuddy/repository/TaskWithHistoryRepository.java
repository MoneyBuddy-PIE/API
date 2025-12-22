/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import moneybuddy.fr.moneybuddy.model.TaskWithHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskWithHistoryRepository extends MongoRepository<TaskWithHistory, String> {}
