package moneybuddy.fr.moneybuddy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import moneybuddy.fr.moneybuddy.model.SubAccountCourse;

@Repository
public interface SubAccountCourseRepository extends MongoRepository<SubAccountCourse, String> {

}
