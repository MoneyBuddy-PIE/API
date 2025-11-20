package moneybuddy.fr.moneybuddy.config;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour le bot Discord.
 * Cette classe initialise la connexion Discord4J avec le token du bot
 * et configure les paramètres nécessaires pour l'envoi de notifications.
 */
@Slf4j
@Configuration
public class DiscordBotConfig {

    @Value("${discord.bot.token}")
    private String botToken;

    @Value("${discord.bot.enabled:true}")
    private boolean botEnabled;

    /**
     * Bean qui crée le client Discord.
     * Ce client sera utilisé pour envoyer des messages au serveur Discord.
     *
     * @return GatewayDiscordClient configuré ou null si le bot est désactivé
     */
    @Bean
    public GatewayDiscordClient discordClient() {
        if (!botEnabled) {
            log.info("Bot Discord désactivé dans la configuration");
            return null;
        }

        try {
            log.info("Initialisation du client Discord...");

            // Création du client Discord avec le token
            GatewayDiscordClient client = DiscordClient.create(botToken)
                    .login()
                    .block();

            if (client != null) {
                log.info("Client Discord initialisé avec succès");

                // Log des informations du bot
                client.getSelf()
                    .map(user -> "Bot connecté: " + user.getUsername())
                    .subscribe(botInfo -> log.info(botInfo));
            }

            return client;

        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation du client Discord: {}", e.getMessage());
            // On ne fait pas planter l'application si Discord ne fonctionne pas
            return null;
        }
    }
}
