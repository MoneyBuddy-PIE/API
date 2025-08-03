package moneybuddy.fr.moneybuddy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import moneybuddy.fr.moneybuddy.model.Chapter;

public interface ChapterRepository extends MongoRepository<Chapter, String> {

}

