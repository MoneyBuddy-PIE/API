package moneybuddy.fr.moneybuddy.repository;

import moneybuddy.fr.moneybuddy.model.BotAction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BotActionRepository extends MongoRepository<BotAction, String> {

    /**
     * Trouve toutes les actions par type
     */
    List<BotAction> findByActionType(String actionType);

    /**
     * Trouve toutes les actions par statut
     */
    List<BotAction> findByStatus(String status);

    /**
     * Trouve toutes les actions par utilisateur
     */
    List<BotAction> findByUserIdentifier(String userIdentifier);

    /**
     * Trouve toutes les actions dans une période donnée
     */
    List<BotAction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Trouve toutes les actions d'un type spécifique pour un utilisateur
     */
    List<BotAction> findByActionTypeAndUserIdentifier(String actionType, String userIdentifier);

    /**
     * Compte le nombre d'actions par statut
     */
    long countByStatus(String status);
}
