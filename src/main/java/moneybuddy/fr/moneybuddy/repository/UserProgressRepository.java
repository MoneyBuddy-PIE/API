package moneybuddy.fr.moneybuddy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import moneybuddy.fr.moneybuddy.model.UserProgress;

@Repository
public interface UserProgressRepository extends MongoRepository<UserProgress, String> {

    UserProgress findBySubAccountId(String subAccountId);

}
