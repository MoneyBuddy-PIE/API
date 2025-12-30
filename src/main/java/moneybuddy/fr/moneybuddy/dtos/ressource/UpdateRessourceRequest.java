/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.ressource;

import lombok.Data;
import moneybuddy.fr.moneybuddy.model.enums.RessourceType;

@Data
public class UpdateRessourceRequest {

  private String title;

  private String url;

  private RessourceType type;
}
