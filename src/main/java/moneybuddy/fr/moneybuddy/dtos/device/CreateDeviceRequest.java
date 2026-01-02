/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.enums.DevicePlatform;

@Data
public class CreateDeviceRequest {

  @NotBlank(message = "UserId est obligatoire.")
  private String userId;

  @NotBlank(message = "Token est obligatoire.")
  private String token;

  @NotNull(message = "DevicePlatform est obligatoire.")
  private DevicePlatform devicePlatform;
}
