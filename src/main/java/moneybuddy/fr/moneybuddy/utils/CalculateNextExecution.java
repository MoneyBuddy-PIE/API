/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.utils;

import java.time.LocalDate;

import moneybuddy.fr.moneybuddy.model.enums.AllowanceFrequency;
import org.springframework.stereotype.Component;

@Component
public class CalculateNextExecution {
  public LocalDate calculateNextExecution(AllowanceFrequency frequency, LocalDate nextExecution) {

    LocalDate current = nextExecution;

    return switch (frequency) {
      case WEEKLY -> current.plusWeeks(1);
      case BIWEEKLY -> current.plusWeeks(2);
      case MONTHLY -> current.plusMonths(1);
    };
  }
}
