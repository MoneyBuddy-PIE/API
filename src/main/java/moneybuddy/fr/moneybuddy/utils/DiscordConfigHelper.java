package moneybuddy.fr.moneybuddy.utils;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.GuildChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utilitaire pour aider à configurer Discord
 * Utilisez cette classe temporairement pour trouver les IDs de vos canaux
 */
@Slf4j
@Component
public class DiscordConfigHelper {

    private final GatewayDiscordClient discordClient;

    @Autowired
    public DiscordConfigHelper(GatewayDiscordClient discordClient) {
        this.discordClient = discordClient;
    }

    /**
     * Affiche tous les canaux du serveur avec leurs IDs
     * Utilisez cette méthode temporairement pour identifier vos canaux
     */
    public void printAllChannelIds() {
        if (discordClient == null) {
            log.warn("Client Discord non configuré");
            return;
        }

        try {
            discordClient.getGuilds()
                .flatMap(Guild::getChannels)
                .cast(GuildChannel.class)
                .subscribe(channel -> {
                    log.info("Canal: {} - ID: {}",
                        channel.getName(),
                        channel.getId().asString());
                });
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des canaux: {}", e.getMessage());
        }
    }

    /**
     * Trouve un canal par son nom
     * @param channelName Nom du canal à chercher
     * @return L'ID du canal trouvé
     */
    public String findChannelIdByName(String channelName) {
        if (discordClient == null) {
            log.warn("Client Discord non configuré");
            return null;
        }

        try {
            return discordClient.getGuilds()
                .flatMap(Guild::getChannels)
                .cast(GuildChannel.class)
                .filter(channel -> channelName.equals(channel.getName()))
                .next()
                .map(channel -> channel.getId().asString())
                .block();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche du canal '{}': {}", channelName, e.getMessage());
            return null;
        }
    }
}
