package moneybuddy.fr.moneybuddy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bot_actions")
public class BotAction {

    @Id
    private String id;

    /**
     * Type d'action (ex: "USER_REGISTRATION", "ACCOUNT_CREATION", "TRANSACTION", etc.)
     */
    private String actionType;

    /**
     * Message envoyé au Discord
     */
    private String message;

    /**
     * ID du canal Discord où le message a été envoyé
     */
    private String channelId;

    /**
     * ID du message Discord
     */
    private String messageId;

    /**
     * Email ou identifiant de l'utilisateur concerné
     */
    private String userIdentifier;

    /**
     * Détails additionnels de l'action (JSON format)
     */
    private String actionDetails;

    /**
     * Statut de l'envoi (SUCCESS, FAILED, PENDING)
     */
    private String status;

    /**
     * Message d'erreur si l'envoi a échoué
     */
    private String errorMessage;

    /**
     * Date de création de l'action
     */
    private LocalDateTime createdAt;

    /**
     * Date de traitement de l'action
     */
    private LocalDateTime processedAt;

    public BotAction(String actionType, String message, String userIdentifier, String actionDetails) {
        this.actionType = actionType;
        this.message = message;
        this.userIdentifier = userIdentifier;
        this.actionDetails = actionDetails;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }
}
