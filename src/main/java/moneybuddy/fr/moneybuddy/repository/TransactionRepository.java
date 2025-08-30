package moneybuddy.fr.moneybuddy.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import moneybuddy.fr.moneybuddy.model.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByChildId(String childId);

    Page<Transaction> findAllByAccountId(String accountId, Pageable pageable);
    Page<Transaction> findAll(Pageable pageable);
}
