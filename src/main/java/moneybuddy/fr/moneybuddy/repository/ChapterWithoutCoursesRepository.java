/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import moneybuddy.fr.moneybuddy.model.ChapterWithoutCourses;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChapterWithoutCoursesRepository
    extends MongoRepository<ChapterWithoutCourses, String> {

  Page<ChapterWithoutCourses> findAllBySubAccountRole(
      Pageable pageable, SubAccountRole subAccountRole);

  @Query(value = "{ 'subAccountRole': ?0, 'locked': false }")
  Page<ChapterWithoutCourses> findAllBySubAccountRoleAndLockedFalse(
      SubAccountRole subAccountRole, Pageable pageable);
}
