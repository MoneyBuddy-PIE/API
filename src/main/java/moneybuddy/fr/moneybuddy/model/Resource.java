/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import lombok.*;
import moneybuddy.fr.moneybuddy.model.enums.RessourceType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
  private String title;
  private String url;
  private RessourceType type;
}
