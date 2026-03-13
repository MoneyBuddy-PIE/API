/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.subAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubAccountDto {

  private String name;

  private String iconStyle;
  private String iconName;
}
