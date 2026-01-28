/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.chapter;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateChapterRequest {
  private String title;
  private String description;
  private SubAccountRole subAccountRole;

  @PositiveOrZero(message = "Level has to be >= 0")
  private int level;

  @PositiveOrZero(message = "Order has to be >= 0")
  private int order;

  @PositiveOrZero(message = "Coin reward has to be >= 0")
  private int coinReward;

  private boolean locked;
  private MultipartFile file;
}
