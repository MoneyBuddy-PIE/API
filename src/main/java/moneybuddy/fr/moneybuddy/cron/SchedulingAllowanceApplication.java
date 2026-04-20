/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.cron;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.model.Allowance;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.repository.AllowanceRepository;
import moneybuddy.fr.moneybuddy.service.DiscordService;
import moneybuddy.fr.moneybuddy.service.IncomeService;
import moneybuddy.fr.moneybuddy.utils.CalculateNextExecution;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulingAllowanceApplication {

  private final AllowanceRepository allowanceRepository;
  private final DiscordService discordService;
  private final CalculateNextExecution calculateNextExecution;
  private final IncomeService incomeService;

  @Scheduled(cron = "0 5 0 * * *")
  public void processAllowances() {
    LocalDate today = LocalDate.now();

    List<Allowance> allowances =
        allowanceRepository
            .findByActiveTrueAndNextExecutionIsNullOrNextExecutionEquals(today)
            .orElse(java.util.Collections.emptyList());

    discordService.SendMessage(
        "CRON JOB - Distribution de l'allocation ! Nombre : " + allowances.size());

    for (Allowance allowance : allowances) {
      allowance.setNextExecution(
          calculateNextExecution.calculateNextExecution(
              allowance.getFrequency(), allowance.getNextExecution()));
      allowanceRepository.save(allowance);

      SubAccount subAccount = allowance.getSubAccount();
      incomeService.increaseSubAccountIncome(subAccount, allowance.getAmount(), allowance);
    }
  }
}
