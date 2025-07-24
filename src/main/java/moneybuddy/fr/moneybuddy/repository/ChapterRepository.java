package moneybuddy.fr.moneybuddy.repository;

import moneybuddy.fr.moneybuddy.model.Chapter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChapterRepository extends MongoRepository<Chapter, String> {
 
     Page<Chapter> findAll(Pageable pageable);

     Chapter findByTitle(String title);
}

