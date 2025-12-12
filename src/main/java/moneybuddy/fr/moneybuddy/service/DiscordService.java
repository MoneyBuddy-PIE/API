package moneybuddy.fr.moneybuddy.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import moneybuddy.fr.moneybuddy.model.SubAccount;

import org.springframework.beans.factory.annotation.Value;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Service
public class DiscordService {
    private JDA jda;
    
    @Value("${discord.application.token}")
    private String token;
    
    @Value("${discord.channel.monitoring}")
    private String monitoring_channel;

    @PostConstruct
    public void init() {
        this.jda = JDABuilder.createDefault(token).build();
    }
    
    public void sendNewAccountMessage(String email, SubAccount subAccount, Boolean isAccount) {
        TextChannel channel = jda.getTextChannelById(monitoring_channel);

        String text = isAccount ? "🆕 Nouveau compte créé par : " : "🆕 Nouveau sous-compte créé par : ";
        int color = isAccount ? 0x684BAB : 0xD3363F;

        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder()
                .setTitle(text + email)
                .setColor(color)
                .setTimestamp(Instant.now())
                .addField("AccountId", subAccount.getId(), true)
                .addField("SubAccountId", subAccount.getId(), true)
                .addField("SubAccountName", subAccount.getName(), true);

            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }
}
