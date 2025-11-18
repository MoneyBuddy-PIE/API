package moneybuddy.fr.moneybuddy.utils.operations;

import org.springframework.stereotype.Component;

import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.enums.DepositType;
import moneybuddy.fr.moneybuddy.model.DepositDetails;
import moneybuddy.fr.moneybuddy.model.Goal;

import moneybuddy.fr.moneybuddy.dtos.Money.AddMoney;

import moneybuddy.fr.moneybuddy.repository.GoalTransactionRepository;

import moneybuddy.fr.moneybuddy.service.MoneyService;

@Component
public class Operations {

    private final GoalTransactionRepository goalTransactionRepository;
    private final MoneyService moneyService;

    public Operations(GoalTransactionRepository goalTransactionRepository, MoneyService moneyService) {
        this.goalTransactionRepository = goalTransactionRepository;
        this.moneyService = moneyService;
    }

    public void updateProgression(Goal goal) {
        Number updateProgression = (goal.getDepositStatement().doubleValue()/goal.getAmount().doubleValue()) * 100;
        goal.setProgression(updateProgression);
    }

    public void updateGoalTransactionHistory(Goal goal, DepositType type, Number amount, Number updatedGoalAmount) {
        DepositDetails depositDetails = DepositDetails.builder()
                                                .goalId(goal.getId())
                                                .parentId(goal.getSubaccountIdParent())
                                                .childId(goal.getSubaccountIdChild())
                                                .accountId(goal.getAccountId())
                                                .type(type)
                                                .amount(amount)
                                                .previousAmount(goal.getDepositStatement())
                                                .newAmount(updatedGoalAmount)
                                                .build();
        goalTransactionRepository.save(depositDetails);
    }

    public void updateAccountBalanceMoney(SubAccount subAccount, String token, String money, boolean transferAddMoney, String description) {
        AddMoney newAddMoneyDetail = AddMoney.builder()
                                        .subAccountId(subAccount.getId())
                                        .amount(money)
                                        .description(description)
                                        .build();

        moneyService.updateMoney(newAddMoneyDetail, token, transferAddMoney);
    }
}
