package moneybuddy.fr.moneybuddy.repository;

import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.enums.PlanType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {
    Optional<Account> findByEmail(String email);

    @Query(value = "{ 'planType': ?0 }")
    Page<Account> findAllByPlanType(PlanType planType, Pageable pageable);

    Page<Account> findAll(Pageable pageable);
}

