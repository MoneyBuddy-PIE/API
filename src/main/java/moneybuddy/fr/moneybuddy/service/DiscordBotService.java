package moneybuddy.fr.moneybuddy.service;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import lombok.extern.slf4j.Slf4j;
import moneybuddy.fr.moneybuddy.repository.BotActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * Service principal du bot Discord qui gère les événements et les commandes.
 *
 * Cette classe :
 * - Démarre automatiquement avec l'application Spring Boot
 * - Écoute les événements Discord (messages, etc.)
 * - Répond aux commandes comme !status, !help
 * - Fournit des statistiques sur l'utilisation du bot
 */
@Slf4j
@Service
public class DiscordBotService implements ApplicationRunner {

    private final GatewayDiscordClient discordClient;
    private final BotActionRepository botActionRepository;

    @Value("${discord.bot.enabled:true}")
    private boolean botEnabled;

    @Value("${discord.bot.prefix:!}")
    private String commandPrefix;

    @Autowired
    public DiscordBotService(
            GatewayDiscordClient discordClient,
            BotActionRepository botActionRepository) {
        this.discordClient = discordClient;
        this.botActionRepository = botActionRepository;
    }

    /**
     * Démarre le bot Discord après le démarrage de l'application.
     * Cette méthode est appelée automatiquement par Spring Boot.
     */
    @Override
    public void run(ApplicationArguments args) {
        if (!botEnabled || discordClient == null) {
            log.info("Bot Discord désactivé ou non configuré");
            return;
        }

        try {
            setupEventListeners();
            log.info("Bot Discord démarré avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du démarrage du bot Discord: {}", e.getMessage());
        }
    }

    /**
     * Configure les listeners pour les événements Discord
     */
    private void setupEventListeners() {
        // Événement quand le bot est prêt
        discordClient.on(ReadyEvent.class)
            .subscribe(event -> {
                User self = event.getSelf();
                log.info("Bot Discord connecté en tant que: {}", self.getUsername());
            });

        // Événement quand un message est créé
        discordClient.on(MessageCreateEvent.class)
            .subscribe(this::handleMessageCreate);
    }

    /**
     * Gère les messages reçus par le bot
     */
    private void handleMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();

        // Ignorer les messages du bot lui-même
        if (message.getAuthor().map(User::isBot).orElse(false)) {
            return;
        }

        String content = message.getContent();

        // Vérifier si c'est une commande (commence par le préfixe)
        if (content.startsWith(commandPrefix)) {
            handleCommand(message, content.substring(commandPrefix.length()).trim());
        }
    }

    /**
     * Gère les commandes du bot
     */
    private void handleCommand(Message message, String command) {
        String[] parts = command.split("\\s+");
        String commandName = parts[0].toLowerCase();

        try {
            switch (commandName) {
                case "help":
                    handleHelpCommand(message);
                    break;
                case "status":
                    handleStatusCommand(message);
                    break;
                case "stats":
                    handleStatsCommand(message);
                    break;
                default:
                    handleUnknownCommand(message, commandName);
            }
        } catch (Exception e) {
            log.error("Erreur lors du traitement de la commande '{}': {}", command, e.getMessage());
            message.getChannel()
                .flatMap(channel -> channel.createMessage("❌ Erreur lors du traitement de la commande."))
                .subscribe();
        }
    }

    /**
     * Commande !help - Affiche les commandes disponibles
     */
    private void handleHelpCommand(Message message) {
        String helpMessage = """
                **🤖 MoneyBuddy Bot - Commandes disponibles :**

                `%shelp` - Affiche cette aide
                `%sstatus` - Vérifie le statut du système
                `%sstats` - Affiche les statistiques des notifications

                **ℹ️ À propos :**
                Je suis le bot officiel de MoneyBuddy. Je vous notifie automatiquement des événements importants comme :
                • 🎉 Création de nouveaux comptes
                • 👶 Création de sous-comptes
                • ❌ Erreurs système
                """.formatted(commandPrefix, commandPrefix, commandPrefix);

        message.getChannel()
            .flatMap(channel -> channel.createMessage(helpMessage))
            .subscribe();
    }

    /**
     * Commande !status - Vérifie le statut du système
     */
    private void handleStatusCommand(Message message) {
        try {
            long totalActions = botActionRepository.count();
            long successfulActions = botActionRepository.countByStatus("SUCCESS");
            long failedActions = botActionRepository.countByStatus("FAILED");

            String statusMessage = String.format("""
                **✅ MoneyBuddy Bot - Statut du système**

                🤖 **Bot :** En ligne et fonctionnel
                📊 **Statistiques :**
                • Total des actions : %d
                • Actions réussies : %d
                • Actions échouées : %d
                • Taux de succès : %.1f%%

                🕐 **Dernière vérification :** %s
                """,
                totalActions,
                successfulActions,
                failedActions,
                totalActions > 0 ? (successfulActions * 100.0 / totalActions) : 0.0,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );

            message.getChannel()
                .flatMap(channel -> channel.createMessage(statusMessage))
                .subscribe();

        } catch (Exception e) {
            log.error("Erreur lors de la vérification du statut: {}", e.getMessage());
            message.getChannel()
                .flatMap(channel -> channel.createMessage("❌ Erreur lors de la vérification du statut."))
                .subscribe();
        }
    }

    /**
     * Commande !stats - Affiche les statistiques détaillées
     */
    private void handleStatsCommand(Message message) {
        try {
            long registrations = botActionRepository.findByActionType("USER_REGISTRATION").size();
            long subAccounts = botActionRepository.findByActionType("SUBACCOUNT_CREATION").size();
            long errors = botActionRepository.findByActionType("SYSTEM_ERROR").size();

            String statsMessage = String.format("""
                **📊 MoneyBuddy Bot - Statistiques détaillées**

                **👥 Activité des utilisateurs :**
                • 🎉 Inscriptions : %d
                • 👶 Sous-comptes créés : %d

                **⚠️ Erreurs système :**
                • ❌ Erreurs rapportées : %d

                **🔍 Pour plus de détails, contactez l'équipe technique.**
                """,
                registrations,
                subAccounts,
                errors
            );

            message.getChannel()
                .flatMap(channel -> channel.createMessage(statsMessage))
                .subscribe();

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques: {}", e.getMessage());
            message.getChannel()
                .flatMap(channel -> channel.createMessage("❌ Erreur lors de la récupération des statistiques."))
                .subscribe();
        }
    }

    /**
     * Gère les commandes inconnues
     */
    private void handleUnknownCommand(Message message, String command) {
        String errorMessage = String.format("❓ Commande inconnue : `%s`\nTapez `%shelp` pour voir les commandes disponibles.",
            command, commandPrefix);

        message.getChannel()
            .flatMap(channel -> channel.createMessage(errorMessage))
            .subscribe();
    }
}
