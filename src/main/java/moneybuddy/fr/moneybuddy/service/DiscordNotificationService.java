package moneybuddy.fr.moneybuddy.service;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;
import moneybuddy.fr.moneybuddy.model.BotAction;
import moneybuddy.fr.moneybuddy.repository.BotActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service responsable de l'envoi de notifications Discord.
 *
 * Ce service :
 * - Envoie des messages formatés au serveur Discord
 * - Sauvegarde toutes les actions dans la base de données
 * - Gère les erreurs et la récupération
 * - Fournit différents types de notifications (succès, erreur, info)
 */
@Slf4j
@Service
public class DiscordNotificationService {

    private final GatewayDiscordClient discordClient;
    private final BotActionRepository botActionRepository;

    @Value("${discord.bot.channel.notifications}")
    private String notificationChannelId;

    @Value("${discord.bot.enabled:true}")
    private boolean botEnabled;

    @Autowired
    public DiscordNotificationService(
            GatewayDiscordClient discordClient,
            BotActionRepository botActionRepository) {
        this.discordClient = discordClient;
        this.botActionRepository = botActionRepository;
    }

    /**
     * Envoie une notification de création de compte utilisateur
     *
     * @param email Email de l'utilisateur
     * @param username Nom d'utilisateur
     */
    public void sendUserRegistrationNotification(String email, String username) {
        String actionType = "USER_REGISTRATION";
        String message = String.format("🎉 **Nouvel utilisateur inscrit !**\n" +
                "📧 Email: `%s`\n" +
                "👤 Nom d'utilisateur: `%s`\n" +
                "📅 Date: %s",
                email, username, getCurrentTimestamp());

        String actionDetails = String.format("{\"email\":\"%s\",\"username\":\"%s\"}", email, username);

        sendNotification(actionType, message, email, actionDetails, Color.GREEN);
    }

    /**
     * Envoie une notification de création de sous-compte
     *
     * @param parentEmail Email du compte parent
     * @param childName Nom du sous-compte
     */
    public void sendSubAccountCreationNotification(String parentEmail, String childName) {
        String actionType = "SUBACCOUNT_CREATION";
        String message = String.format("👶 **Nouveau sous-compte créé !**\n" +
                "👨‍👩‍👧‍👦 Compte parent: `%s`\n" +
                "👤 Nom du sous-compte: `%s`\n" +
                "📅 Date: %s",
                parentEmail, childName, getCurrentTimestamp());

        String actionDetails = String.format("{\"parentEmail\":\"%s\",\"childName\":\"%s\"}", parentEmail, childName);

        sendNotification(actionType, message, parentEmail, actionDetails, Color.BLUE);
    }

    /**
     * Envoie une notification d'erreur système
     *
     * @param errorType Type d'erreur
     * @param errorMessage Message d'erreur
     * @param userIdentifier Identifiant de l'utilisateur concerné (optionnel)
     */
    public void sendErrorNotification(String errorType, String errorMessage, String userIdentifier) {
        String actionType = "SYSTEM_ERROR";
        String message = String.format("❌ **Erreur système détectée !**\n" +
                "🚨 Type: `%s`\n" +
                "📝 Message: `%s`\n" +
                "👤 Utilisateur: `%s`\n" +
                "📅 Date: %s",
                errorType, errorMessage, userIdentifier != null ? userIdentifier : "N/A", getCurrentTimestamp());

        String actionDetails = String.format("{\"errorType\":\"%s\",\"errorMessage\":\"%s\"}", errorType, errorMessage);

        sendNotification(actionType, message, userIdentifier, actionDetails, Color.RED);
    }

    /**
     * Méthode générique pour envoyer une notification Discord
     *
     * @param actionType Type d'action
     * @param message Message à envoyer
     * @param userIdentifier Identifiant de l'utilisateur
     * @param actionDetails Détails de l'action en JSON
     * @param color Couleur de l'embed Discord
     */
    private void sendNotification(String actionType, String message, String userIdentifier, String actionDetails, Color color) {
        // Créer l'action en base de données
        BotAction botAction = new BotAction(actionType, message, userIdentifier, actionDetails);

        if (!botEnabled || discordClient == null) {
            log.warn("Bot Discord désactivé - Action sauvegardée mais pas envoyée: {}", actionType);
            botAction.setStatus("DISABLED");
            botAction.setErrorMessage("Bot Discord désactivé");
            botActionRepository.save(botAction);
            return;
        }

        try {
            // Envoyer le message Discord
            discordClient.getChannelById(snowflake(notificationChannelId))
                .cast(TextChannel.class)
                .flatMap(channel -> channel.createMessage()
                    .withEmbeds(EmbedCreateSpec.builder()
                        .color(color)
                        .title("MoneyBuddy - Notification")
                        .description(message)
                        .footer("MoneyBuddy Bot", null)
                        .timestamp(java.time.Instant.now())
                        .build()))
                .subscribe(
                    sentMessage -> {
                        // Succès
                        botAction.setStatus("SUCCESS");
                        botAction.setChannelId(notificationChannelId);
                        botAction.setMessageId(sentMessage.getId().asString());
                        botAction.setProcessedAt(LocalDateTime.now());
                        botActionRepository.save(botAction);
                        log.info("Notification Discord envoyée avec succès: {}", actionType);
                    },
                    error -> {
                        // Erreur
                        botAction.setStatus("FAILED");
                        botAction.setErrorMessage(error.getMessage());
                        botAction.setProcessedAt(LocalDateTime.now());
                        botActionRepository.save(botAction);
                        log.error("Erreur lors de l'envoi de la notification Discord: {}", error.getMessage());
                    }
                );

        } catch (Exception e) {
            botAction.setStatus("FAILED");
            botAction.setErrorMessage(e.getMessage());
            botAction.setProcessedAt(LocalDateTime.now());
            botActionRepository.save(botAction);
            log.error("Erreur lors de l'envoi de la notification Discord: {}", e.getMessage());
        }
    }

    /**
     * Convertit une chaîne en Snowflake Discord
     */
    private discord4j.common.util.Snowflake snowflake(String id) {
        return discord4j.common.util.Snowflake.of(id);
    }

    /**
     * Obtient le timestamp actuel formaté
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
