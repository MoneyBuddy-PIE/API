package moneybuddy.fr.moneybuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class MoneybuddyApplication {

	public static void LoadDotEnv() {
		Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});
    }

	public static void main(String[] args) {
		LoadDotEnv();
		SpringApplication.run(MoneybuddyApplication.class, args);
	}

}
