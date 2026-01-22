/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChapterRepository extends MongoRepository<Chapter, String> {

  @Query(value = "{ 'subAccountRole': ?0, 'locked': false }")
  Page<Chapter> findAllBySubAccountRoleAndLockedFalse(
      SubAccountRole subAccountRole, Pageable pageable);

  @Query(value = "{}", fields = "{ 'courses' : 0 }")
  Page<Chapter> findAllWithoutCourses(Pageable pageable);
}
