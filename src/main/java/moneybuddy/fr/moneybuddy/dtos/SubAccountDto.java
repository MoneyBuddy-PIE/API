/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.utils.CheckByRegex;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccountDto {
  private static final CheckByRegex checkByRegex = new CheckByRegex();

  @NotNull(message = "Name is mandatory")
  private String name;

  @NotNull(message = "Role is mandatory")
  private SubAccountRole role;

  private String iconStyle;
  private String iconName;

  private String pin;

  @AssertTrue(message = "PIN must be exactly 4 digits")
  private boolean isPinValid() {
    return pin != null && pin.length() == 4 && checkByRegex.validate(pin, "^\\d{4}$");
  }
}
