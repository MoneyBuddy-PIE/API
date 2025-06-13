package moneybuddy.fr.moneybuddy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import moneybuddy.fr.moneybuddy.model.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
 
}
