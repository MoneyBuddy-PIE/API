/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.CreateGoalRequest;
import moneybuddy.fr.moneybuddy.dtos.GoalMoneyRequest;
import moneybuddy.fr.moneybuddy.exception.GoalAlreadyCompletedException;
import moneybuddy.fr.moneybuddy.exception.GoalAlreadyUsedException;
import moneybuddy.fr.moneybuddy.exception.GoalAmountExceededException;
import moneybuddy.fr.moneybuddy.exception.GoalNotCompletedException;
import moneybuddy.fr.moneybuddy.exception.GoalNotFoundException;
import moneybuddy.fr.moneybuddy.exception.InsufficientBalanceException;
import moneybuddy.fr.moneybuddy.exception.SubAccountNotFoundException;
import moneybuddy.fr.moneybuddy.exception.UnauthorizedGoalAccessException;
import moneybuddy.fr.moneybuddy.model.Goal;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.enums.GoalStatus;
import moneybuddy.fr.moneybuddy.model.enums.Role;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.enums.TransactionType;
import moneybuddy.fr.moneybuddy.repository.GoalRepository;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import moneybuddy.fr.moneybuddy.utils.operations.Operations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalService {

  private final MongoTemplate mongoTemplate;
  private final GoalRepository goalRepository;
  private final SubAccountRepository subAccountRepository;
  private final JwtService jwtService;
  private final Operations operations;

  public ResponseEntity<AuthResponse> createGoal(CreateGoalRequest request, String token) {
    String subAccountId = jwtService.extractSubAccountId(token);
    String accountId = jwtService.extractSubAccountAccountId(token);

    System.out.println("SubAccountId: " + subAccountId);
    System.out.println("AccountId: " + accountId);

    Goal goal =
        Goal.builder()
            .name(request.getName())
            .amount(request.getAmount())
            .emoji(request.getEmoji() != null ? request.getEmoji() : null)
            .subaccountIdChild(subAccountId)
            .accountId(accountId)
            .createdAt(LocalDateTime.now())
            .build();

    System.out.println("Goal: " + goal);

    goalRepository.save(goal);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(AuthResponse.builder().error("Objectif d'épargne créé avec succès").build());
  }

  public ResponseEntity<Goal> modifyGoal(CreateGoalRequest request, String token, String goalId) {
    String subAccountId = jwtService.extractSubAccountId(token);
    Goal goal = goalRepository.findByIdAndSubaccountIdChild(goalId, subAccountId);

    if (goal == null) {
      throw new GoalNotFoundException(goalId);
    }

    goal.setName(
        Optional.ofNullable(request.getName()).filter(s -> !s.isEmpty()).orElse(goal.getName()));

    goal.setAmount(Optional.ofNullable(request.getAmount()).orElse(goal.getAmount()));

    goal.setEmoji(
        Optional.ofNullable(request.getEmoji()).filter(s -> !s.isEmpty()).orElse(goal.getEmoji()));

    goal.setUpdatedAt(LocalDateTime.now());

    Goal updatedGoal = goalRepository.save(goal);
    return ResponseEntity.status(HttpStatus.OK).body(updatedGoal);
  }

  public ResponseEntity<Goal> getGoal(String id) {
    Goal goal = goalRepository.findById(id).orElseThrow(() -> new GoalNotFoundException(id));

    return ResponseEntity.status(HttpStatus.OK).body(goal);
  }

  public ResponseEntity<String> deleteGoal(String token, String goalId) {
    String subAccountId = jwtService.extractSubAccountAccountId(token);

    goalRepository.findById(goalId).orElseThrow(() -> new GoalNotFoundException(goalId));

    goalRepository.deleteByIdAndSubaccountIdChild(goalId, subAccountId);

    return ResponseEntity.status(HttpStatus.OK).body("Objectif d'épargne supprimé avec succès");
  }

  public ResponseEntity<List<Goal>> getGoals(
      String token, String childId, GoalStatus goalStatus, Number progression, String accountId) {
    boolean isAdmin = Role.ADMIN.equals(jwtService.extractAccountRole(token));
    boolean isChild = SubAccountRole.CHILD.equals(jwtService.extractSubAccountRole(token));
    boolean isParent =
        SubAccountRole.PARENT.equals(jwtService.extractSubAccountRole(token))
            || SubAccountRole.OWNER.equals(jwtService.extractSubAccountRole(token));

    String mainAccountId = jwtService.extractSubAccountAccountId(token);
    String subAccountId = jwtService.extractSubAccountId(token);

    Criteria criteria = new Criteria();

    if (isChild) criteria.and("subaccountIdChild").is(subAccountId);

    if (isParent) {
      criteria.and("accountId").is(mainAccountId);
      if (childId != null) criteria.and("subaccountIdChild").is(childId);
    }

    if (isAdmin && accountId != null) criteria.and("accountId").is(accountId);

    if (goalStatus != null) criteria.and("goalStatus").is(goalStatus);

    if (progression != null) criteria.and("progression").is(progression);

    Query query = new Query(criteria);
    List<Goal> goals = mongoTemplate.find(query, Goal.class);

    return ResponseEntity.status(HttpStatus.OK).body(goals);
  }

  public ResponseEntity<String> addGoalMoney(
      GoalMoneyRequest request, String token, String goalId) {
    String subAccountIdChild = jwtService.extractSubAccountId(token);

    Goal goal = goalRepository.findByIdAndSubaccountIdChild(goalId, subAccountIdChild);
    if (goal == null) {
      throw new GoalNotFoundException(goalId);
    }

    SubAccount subAccount =
        subAccountRepository
            .findById(subAccountIdChild)
            .orElseThrow(() -> new SubAccountNotFoundException(subAccountIdChild));

    if (GoalStatus.USED.equals(goal.getGoalStatus())) {
      throw new GoalAlreadyUsedException(goal.getName());
    }

    if (goal.getProgression().intValue() >= 100) {
      goal.setGoalStatus(GoalStatus.DONE);
      goalRepository.save(goal);
      throw new GoalAlreadyCompletedException(goal.getName());
    }

    Float accountBalance = Float.parseFloat(subAccount.getMoney());
    if (request.getTransferMoney() > accountBalance) {
      throw new InsufficientBalanceException(
          String.format(
              "Solde insuffisant. Disponible: %.2f€, Demandé: %.2f€",
              accountBalance, request.getTransferMoney()));
    }

    Float newTotal = request.getTransferMoney() + goal.getDepositStatement();
    if (newTotal > goal.getAmount()) {
      throw new GoalAmountExceededException(
          goal.getDepositStatement(), goal.getAmount(), request.getTransferMoney());
    }

    Float newDepositMoney = goal.getDepositStatement() + request.getTransferMoney();
    operations.updateProgression(goal, newDepositMoney);

    operations.updateGoalTransactionHistory(
        goal, TransactionType.CREDIT, request.getTransferMoney(), newDepositMoney);
    goal.setDepositStatement(newDepositMoney);
    operations.updateAccountBalanceMoney(
        subAccount,
        token,
        request.getTransferMoney().toString(),
        false,
        "Dépôt d'argent pour l'objectif d'épargne: " + goal.getName());

    goal.setUpdatedAt(LocalDateTime.now());
    goalRepository.save(goal);

    return ResponseEntity.status(HttpStatus.ACCEPTED).body("Montant ajouté avec succès.");
  }

  public ResponseEntity<String> removeGoalMoney(
      GoalMoneyRequest request, String token, String goalId) {
    String subAccountIdChild = jwtService.extractSubAccountId(token);

    Goal goal = goalRepository.findByIdAndSubaccountIdChild(goalId, subAccountIdChild);
    if (goal == null) {
      throw new GoalNotFoundException(goalId);
    }

    SubAccount subAccount =
        subAccountRepository
            .findById(goal.getSubaccountIdChild())
            .orElseThrow(() -> new SubAccountNotFoundException(goal.getSubaccountIdChild()));

    if (GoalStatus.USED.equals(goal.getGoalStatus())) {
      throw new GoalAlreadyUsedException(goal.getName());
    }

    if (goal.getProgression().intValue() >= 100) {
      goal.setGoalStatus(GoalStatus.ACTIVATED);
    }

    if (request.getTransferMoney() > goal.getDepositStatement()) {
      throw new InsufficientBalanceException(
          String.format(
              "Montant insuffisant dans l'épargne. Disponible: %.2f€, Demandé: %.2f€",
              goal.getDepositStatement(), request.getTransferMoney()));
    }

    Float newDepositMoney = goal.getDepositStatement() - request.getTransferMoney();
    operations.updateProgression(goal, newDepositMoney);

    operations.updateGoalTransactionHistory(
        goal, TransactionType.DEBIT, request.getTransferMoney(), newDepositMoney);
    goal.setDepositStatement(newDepositMoney);
    operations.updateAccountBalanceMoney(
        subAccount,
        token,
        request.getTransferMoney().toString(),
        true,
        "Retrait d'argent de l'objectif d'épargne: " + goal.getName());

    goal.setUpdatedAt(LocalDateTime.now());
    goalRepository.save(goal);

    return ResponseEntity.status(HttpStatus.ACCEPTED).body("Montant retiré avec succès.");
  }

  public ResponseEntity<String> confirmSavingMoneyTransfer(String token, String goalId) {
    SubAccountRole role = jwtService.extractSubAccountRole(token);
    String subAccountIdChild = jwtService.extractSubAccountId(token);

    if (!SubAccountRole.CHILD.equals(role)) {
      throw new UnauthorizedGoalAccessException(
          "Seul un compte enfant peut transférer l'argent d'un objectif d'épargne");
    }

    Goal goal = goalRepository.findByIdAndSubaccountIdChild(goalId, subAccountIdChild);
    if (goal == null) {
      throw new GoalNotFoundException(goalId);
    }

    SubAccount subAccount =
        subAccountRepository
            .findById(subAccountIdChild)
            .orElseThrow(() -> new SubAccountNotFoundException(subAccountIdChild));

    // Vérifier que l'objectif n'a pas déjà été utilisé
    if (GoalStatus.USED.equals(goal.getGoalStatus())) {
      throw new GoalAlreadyUsedException(goal.getName());
    }

    // Vérifier que l'objectif est terminé
    if (!GoalStatus.DONE.equals(goal.getGoalStatus())) {
      throw new GoalNotCompletedException(
          goal.getName(), goal.getDepositStatement(), goal.getAmount());
    }

    // Effectuer le transfert
    if (GoalStatus.DONE.equals(goal.getGoalStatus())
        && goal.getDepositStatement().doubleValue() == goal.getAmount().doubleValue()
        && goal.getProgression().intValue() == 100) {
      operations.updateGoalTransactionHistory(
          goal, TransactionType.DEBIT, goal.getAmount(), goal.getAmount());
      operations.updateAccountBalanceMoney(
          subAccount,
          token,
          goal.getDepositStatement().toString(),
          true,
          "Transfer du total de l'argent de l'objectif " + goal.getName() + " dans mon solde.");
    }

    goal.setGoalStatus(GoalStatus.USED);
    goal.setUpdatedAt(LocalDateTime.now());
    goalRepository.save(goal);

    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body("Transfert d'argent de l'épargne vers le solde effectué avec succès");
  }
}
