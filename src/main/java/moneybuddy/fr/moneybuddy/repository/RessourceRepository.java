/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import moneybuddy.fr.moneybuddy.model.Ressource;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RessourceRepository extends MongoRepository<Ressource, String> {

  void deleteAllByCourseId(String courseId);
}
