/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequest {

  @NotBlank(message = "ChapterId is mandatory")
  private String chapterId;

  @NotBlank(message = "Title is mandatory")
  private String title;

  @Positive(message = "Readtime has to be > 0")
  private int readTime;

  @PositiveOrZero(message = "Order has to be >= 0")
  private int order;

  @NotNull(message = "Image is mandatory")
  private MultipartFile file;

  @PositiveOrZero(message = "CoinReward has to be >= 0")
  private int coinReward;

  @PositiveOrZero(message = "Level has to be >= 0")
  private int level;
}
