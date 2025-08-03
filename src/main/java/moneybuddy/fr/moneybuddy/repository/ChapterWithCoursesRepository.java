package moneybuddy.fr.moneybuddy.repository;

import moneybuddy.fr.moneybuddy.model.ChapterWithCourses;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChapterWithCoursesRepository extends MongoRepository<ChapterWithCourses, String> {

}

