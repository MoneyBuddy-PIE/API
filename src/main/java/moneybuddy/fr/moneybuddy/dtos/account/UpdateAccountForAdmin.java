/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.account;

import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.enums.PlanType;
import moneybuddy.fr.moneybuddy.model.enums.Role;

@Data
@Builder
public class UpdateAccountForAdmin {

  Role role;
  PlanType planType;

  boolean subscriptionStatus;
}
