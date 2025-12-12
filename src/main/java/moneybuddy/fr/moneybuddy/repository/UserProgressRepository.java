/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import moneybuddy.fr.moneybuddy.model.UserProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProgressRepository extends MongoRepository<UserProgress, String> {

  UserProgress findBySubAccountId(String subAccountId);
}
