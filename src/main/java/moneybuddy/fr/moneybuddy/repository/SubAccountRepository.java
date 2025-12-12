/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.repository;

import java.util.List;

import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubAccountRepository extends MongoRepository<SubAccount, String> {

  List<SubAccount> findByIsActive(boolean isActive);

  List<SubAccount> findByRole(SubAccountRole role);

  SubAccount findByAccountIdAndRole(String accountId, SubAccountRole role);

  long deleteAllByAccountId(String accountId);
}
