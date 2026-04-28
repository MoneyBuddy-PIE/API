/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.course;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateCourseRequest {
  private String title;
  private boolean locked;

  @PositiveOrZero(message = "Readtime has to be > 0")
  private int readTime;

  @PositiveOrZero(message = "Level has to be >= 0")
  private int level;

  @PositiveOrZero(message = "Order has to be >= 0")
  private int order;

  @PositiveOrZero(message = "coinReward has to be >= 0")
  private int coinReward;

  private MultipartFile file;
}
