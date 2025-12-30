/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.chapter;

import jakarta.validation.constraints.*;
import lombok.*;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChapterRequest {
  @NotBlank(message = "Title is mandatory")
  @Size(max = 100, message = "Title cannot exceed 100 characters")
  private String title;

  @NotBlank(message = "Description is mandatory")
  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  @PositiveOrZero(message = "Order must be positive or zero")
  private int order;

  @PositiveOrZero(message = "Level must be positive or zero")
  private int level;

  @PositiveOrZero(message = "CoinReward must be positive or zero")
  private int coinReward;

  @NotNull(message = "Attach a subAccount role")
  private SubAccountRole subAccountRole;

  @NotNull(message = "Image is mandatory")
  private MultipartFile file;
}
