/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import java.util.Optional;

import moneybuddy.fr.moneybuddy.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CourseRepository extends MongoRepository<Course, String> {

  @Query(value = "{ 'chapterId': ?0, 'locked': false }")
  Optional<Page<Course>> findAllByChapterIdAndLockedFalse(String chapterId, Pageable pageable);

  Page<Course> findAll(Pageable pageable);
}
