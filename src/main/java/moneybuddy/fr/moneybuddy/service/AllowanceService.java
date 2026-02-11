/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.Allowance.CreateAllowance;
import moneybuddy.fr.moneybuddy.dtos.Allowance.UpdateAllowance;
import moneybuddy.fr.moneybuddy.exception.AllowanceNotFoundException;
import moneybuddy.fr.moneybuddy.exception.NoRight;
import moneybuddy.fr.moneybuddy.model.Allowance;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.enums.AllowanceFrequency;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.repository.AllowanceRepository;
import moneybuddy.fr.moneybuddy.utils.CalculateNextExecution;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AllowanceService {

  private final AllowanceRepository allowanceRepository;
  private final JwtService jwtService;
  private final SubAccountService subAccountService;
  private final CalculateNextExecution calculateNextExecution;

  public Allowance createAllowance(CreateAllowance req, String token) {

    SubAccountRole role = jwtService.extractSubAccountRole(token);
    if (role.equals(SubAccountRole.CHILD)) throw new NoRight();

    String accountId = jwtService.extractSubAccountAccountId(token);
    String subAccountId = jwtService.extractSubAccountId(token);

    LocalDate startDate = req.getStartDate() != null ? req.getStartDate() : LocalDate.now();
    boolean active = req.isActive() ? req.isActive() : true;

    LocalDate nextExecution =
        calculateNextExecution.calculateNextExecution(req.getFrequency(), startDate);

    SubAccount subAccount = subAccountService.getById(subAccountId);

    Allowance allowance =
        Allowance.builder()
            .accountId(accountId)
            .subAccountId(subAccountId)
            .subAccount(subAccount)
            .subAccountIdChild(req.getSubAccountIdChild())
            .amount(req.getAmount())
            .active(active)
            .frequency(req.getFrequency())
            .startDate(startDate)
            .nextExecution(nextExecution)
            .build();

    if (req.getFrequency().equals(AllowanceFrequency.WEEKLY))
      allowance.setWeeklyDay(req.getWeeklyDay());

    return allowanceRepository.save(allowance);
  }

  public List<Allowance> getAll(String token) {
    String accountId = jwtService.extractSubAccountAccountId(token);
    return allowanceRepository
        .findAllByAccountId(accountId)
        .orElseThrow(() -> new AllowanceNotFoundException());
  }

  public Allowance getById(String id) {
    return allowanceRepository.findById(id).orElseThrow(() -> new AllowanceNotFoundException(id));
  }

  public Allowance updateAllowance(UpdateAllowance req, String id, String token) {
    SubAccountRole role = jwtService.extractSubAccountRole(token);
    if (role == SubAccountRole.CHILD) throw new NoRight();

    Allowance allowance = getById(id);

    if (req.getAmount() != null) allowance.setAmount(req.getAmount());

    if (req.getFrequency() != null) {
      allowance.setFrequency(req.getFrequency());
      if (req.getFrequency() == AllowanceFrequency.WEEKLY) {
        allowance.setWeeklyDay(req.getWeeklyDay());
      } else {
        allowance.setWeeklyDay(null);
      }
    }

    if (req.getFrequency() == AllowanceFrequency.WEEKLY && req.getWeeklyDay() != null)
      allowance.setWeeklyDay(req.getWeeklyDay());

    if (req.getStartDate() != null) allowance.setStartDate(req.getStartDate());

    if (req.getSubAccountIdChild() != null)
      allowance.setSubAccountIdChild(req.getSubAccountIdChild());

    allowance.setActive(req.isActive());
    allowance.setUpdatedAt(LocalDateTime.now());
    return allowanceRepository.save(allowance);
  }
}
