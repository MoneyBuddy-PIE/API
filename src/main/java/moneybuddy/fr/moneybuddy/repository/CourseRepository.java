package moneybuddy.fr.moneybuddy.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    List<Course> findAllBySubAccountRole(SubAccountRole subAccountRole);
}
