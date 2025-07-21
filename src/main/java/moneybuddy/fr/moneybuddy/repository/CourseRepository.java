package moneybuddy.fr.moneybuddy.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

// import java.util.List;
// import java.util.Optional;

import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.SubAccountRole;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
 
    //  List<Course> findBySubaccountIdChild(String id);
    //  List<Course> findBySubaccountIdParent(String id);
    //  List<Course> findByAccountId(String id);
    // List<Course> findByIdAndSubaccountIdParent(String id, String subAccountId);

    List<Course> findAllBySubAccountRole(SubAccountRole subAccountRole);

    // Optional<Course> findByIdAndSubaccountIdParent(String id, String subAccountId);
}
