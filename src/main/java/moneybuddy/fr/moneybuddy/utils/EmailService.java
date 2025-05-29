package moneybuddy.fr.moneybuddy.utils;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    @Value("${resend.email}")
    private String email;

    @Value("${resend.api_key}")
    private String apiKey;

    @Value("${domain}")
    private String domain;
    
    private String notReplyEmail;
    private String contactEmail;

    @PostConstruct
    private void initEmails() {
        this.notReplyEmail = "not-reply" + email;
        this.contactEmail = "hello" + email;
    }

    public String sendEmail(String to, String fromEmail, String subject, String htmlContent) {
        Resend resend = new Resend(apiKey);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(to)
                .subject(subject)
                .html(htmlContent)
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(params);
            return "Email envoyé avec ID : " + response.getId();
        } catch (ResendException e) {
            throw new RuntimeException("Échec d'envoi de l'email : " + e.getMessage(), e);
        }
    }


    public Void resetPasswordEmail(String to, String token) {
        String link = "https://"+ domain + "?token=" + token;

        String htmlContent = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                <h2>Réinitialisation de votre mot de passe</h2>
                <p>Bonjour,</p>
                <p>Nous avons reçu une demande de réinitialisation de votre mot de passe.</p>
                <p>Vous pouvez cliquer sur le bouton ci-dessous pour créer un nouveau mot de passe :</p>
                <a href="%s" style="display: inline-block; margin-top: 15px; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;">
                    Réinitialiser le mot de passe
                </a>
                <p style="margin-top: 20px;">Si vous n'avez pas demandé cette réinitialisation, vous pouvez ignorer cet email.</p>
                <p>Cordialement,<br>L'équipe MoneyBuddy</p>
            </div>
        """.formatted(link);

        sendEmail(to, notReplyEmail, "Réinitialisation de votre mot de passe", htmlContent);
        return null;
    }

    public Void welcomeEmail(String to) {
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                <h2>Bienvenue chez MoneyBuddy 🎉</h2>
                <p>Bonjour,</p>
                <p>Nous sommes ravis de vous compter parmi nos utilisateurs !</p>
                <p>Avec MoneyBuddy, vous pouvez mieux gérer vos finances et atteindre vos objectifs plus rapidement.</p>
                <p style="margin-top: 20px;">
                    Si vous avez la moindre question ou besoin d’aide, n’hésitez pas à nous contacter.
                </p>
                <p>Cordialement,<br>L’équipe MoneyBuddy</p>
            </div>
        """;

        sendEmail(to, contactEmail,"Bienvenue chez MoneyBuddy !", htmlContent);
        return null;
    }

}