/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.Instant;

import jakarta.annotation.PostConstruct;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DiscordService {
  private JDA jda;

  @Value("${discord.application.token}")
  private String token;

  @Value("${discord.channel.monitoring.auth}")
  private String monitoring_auth_channel;

  @Value("${discord.channel.monitoring.errors}")
  private String monitoring_errors_channel;

  @PostConstruct
  public void init() {
    this.jda = JDABuilder.createDefault(token).build();
  }

  public void sendNewAccountMessage(String email, SubAccount subAccount, Boolean isAccount) {
    TextChannel channel = jda.getTextChannelById(monitoring_auth_channel);

    String text =
        isAccount ? "🆕 Nouveau compte créé par : " : "🆕 Nouveau sous-compte créé par : ";
    int color = isAccount ? 0x684BAB : 0xD3363F;

    if (channel != null) {
      EmbedBuilder embed =
          new EmbedBuilder()
              .setTitle(text + email)
              .setColor(color)
              .setTimestamp(Instant.now())
              .addField("AccountId", subAccount.getId(), true)
              .addField("SubAccountId", subAccount.getId(), true)
              .addField("SubAccountName", subAccount.getName(), true);

      channel.sendMessageEmbeds(embed.build()).queue();
    }
  }

  public void sendErroMessage(String errorMessaage, HttpStatus status) {
    TextChannel channel = jda.getTextChannelById(monitoring_errors_channel);

    if (channel != null) {
      EmbedBuilder embed =
          new EmbedBuilder()
              .setTitle("🚨 ERROR")
              .setColor(0xD3363F)
              .setTimestamp(Instant.now())
              .addField("Status", status.toString(), false)
              .addField("Message", errorMessaage, false);

      channel.sendMessageEmbeds(embed.build()).queue();
    }
  }
}
