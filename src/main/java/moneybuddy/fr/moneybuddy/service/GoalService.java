package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
// import moneybuddy.fr.moneybuddy.dtos.GoalComplete;
import moneybuddy.fr.moneybuddy.dtos.CreateGoalRequest;
import moneybuddy.fr.moneybuddy.dtos.GoalMoneyRequest;
import moneybuddy.fr.moneybuddy.model.enums.DepositType;
import moneybuddy.fr.moneybuddy.model.enums.Role;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.Goal;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import moneybuddy.fr.moneybuddy.utils.operations.Operations;
import moneybuddy.fr.moneybuddy.repository.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final MongoTemplate mongoTemplate;
    private final GoalRepository goalRepository;
    private final SubAccountRepository subAccountRepository;
    private final JwtService jwtService;
    private final Operations operations;

    public ResponseEntity<AuthResponse> response(String message, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(AuthResponse.builder()
                    .error(message)
                    .build());
    }

    public ResponseEntity<AuthResponse> createGoal (CreateGoalRequest request, String token) {        
        SubAccountRole role = jwtService.extractSubAccountRole(token);
        String subAccountIdParent = jwtService.extractSubAccountId(token);
        String accountId = jwtService.extractSubAccountAccountId(token);

        if (!SubAccountRole.CHILD.equals(role)) {
            return response("Vous n'avez pas les droits", HttpStatus.FORBIDDEN);
        }

        Goal goal = Goal.builder()
                    .name(request.getName())
                    .amount(request.getAmount())
                    .emoji(request.getEmoji() != null ? request.getEmoji() : null)
                    .subaccountIdParent(subAccountIdParent) // Comment différencier subAccountId du parent avec enfant depuis token ?
                    .subaccountIdChild(request.getSubAccountId())
                    .accountId(accountId)
                    .createdAt(LocalDateTime.now())
                    .build();

        goalRepository.save(goal);
        return response("L'objectif est crée.", HttpStatus.CREATED);
    }

    public ResponseEntity<Goal> modifyGoal (CreateGoalRequest request, String token, String goalId) {
        SubAccountRole role = jwtService.extractSubAccountRole(token);
        String subAccountIdChild = request.getSubAccountId();

        Goal goal = goalRepository.findByIdAndSubaccountIdChild(goalId, subAccountIdChild);

        if (!SubAccountRole.CHILD.equals(role) && !goal.isActive() && !goal.isDone()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        goal.setName(request.getName());
        goal.setAmount(request.getAmount());
        goal.setEmoji(request.getEmoji());
        goal.setSubaccountIdChild(request.getSubAccountId());
        goal.setUpdatedAt(LocalDateTime.now());

        Goal updatedGoal = goalRepository.save(goal);
        return ResponseEntity.status(HttpStatus.OK).body(updatedGoal);
    }

    public ResponseEntity<Goal> getGoal (String id) {
        Optional<Goal> goal = goalRepository.findById(id);

        return goal.isPresent() ? 
            ResponseEntity.status(HttpStatus.ACCEPTED).body(goal.get())
            : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<String> deleteGoal (String token, String goalId) {
        if (!SubAccountRole.CHILD.equals(jwtService.extractSubAccountRole(token))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vous n'avez pas les droits");
        }
        goalRepository.deleteById(goalId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Objectif supprimé avec succès.");
    }

    public ResponseEntity<List<Goal>> getGoals (
        String token, 
        String childId,
        String parentId,
        String accountId,
        Boolean isActive,
        Boolean isDone,
        Boolean useSavingMoney, 
        Number progression
    ) {

        
        Role role = jwtService.extractAccountRole(token);
        role = Role.ADMIN.equals(role) ? role : Role.USER;
        boolean isAdmin = Role.ADMIN.equals(role);

        boolean isParent = SubAccountRole.PARENT.equals(jwtService.extractSubAccountRole(token));

        Criteria criteria = new Criteria();

        if(isAdmin) {
            // Admin can see all goals subAccountsIdParent and subAccountsIdChild
            criteria.all("subaccountIdParent");
            criteria.all("subaccountIdChild");
        }

        if (isAdmin || ((childId != null && !childId.isEmpty()) || (parentId != null && !parentId.isEmpty()))) {
            criteria.and("accountId").is(accountId);
            criteria.and("subaccountIdChild").is(childId);
        }

        if (isAdmin || (childId != null && !childId.isEmpty())) {
            criteria.and("subaccountIdChild").is(childId);
        }

        if (isAdmin || (parentId != null && !parentId.isEmpty())) {
            criteria.and("subaccountIdParent").is(parentId);
        }

        if (isParent || (childId != null && !childId.isEmpty())) {
            criteria.and("subaccountIdChild").is(childId);
        }

        if (isActive != null) {
            criteria.and("isActive").is(isActive);
        }

        if (isDone != null) {
            criteria.and("isDone").is(isDone);
        }

        if (useSavingMoney != null) {
            criteria.and("useSavingMoney").is(useSavingMoney);
        }

        if (progression != null) {
            criteria.and("progression").is(progression);
        }

        Query query = new Query(criteria);
        List<Goal> goals = mongoTemplate.find(query, Goal.class);

        return ResponseEntity.status(HttpStatus.OK).body(goals);
    }

    public ResponseEntity<String> addGoalMoney (GoalMoneyRequest request, String token, String goalId) {
        SubAccountRole role = jwtService.extractSubAccountRole(token);
        String subAccountIdChild = jwtService.extractSubAccountId(token);

        Goal goal = goalRepository.findByIdAndSubaccountIdChild(goalId, subAccountIdChild);
        SubAccount subAccount = subAccountRepository.findById(subAccountIdChild).orElseThrow();

        if (!SubAccountRole.CHILD.equals(role) && goal.isUseSavingMoney() && (!goal.isActive() || !goal.isDone())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vous n'avez pas les droits");
        }

        if(goal.getProgression().intValue() == 100) {
            goal.setDone(true);
            goal.setActive(false);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("L'objectif est atteint.");
        }

        if (request.getTransferMoney().doubleValue() > Double.parseDouble(subAccount.getMoney())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La somme à ajouter ne doit pas dépassé le solde du compte");
        }

        Integer depositStatement = Integer.parseInt(String.valueOf(goal.getDepositStatement().doubleValue()));
        Integer transferMoney = Integer.parseInt(String.valueOf(request.getTransferMoney().doubleValue()));

        Number newDepositMoney = Integer.sum(depositStatement, transferMoney);

        operations.updateGoalTransactionHistory(goal, DepositType.DEPOSIT, request.getTransferMoney(), newDepositMoney);
        goal.setDepositStatement(newDepositMoney);
        operations.updateProgression(goal);
        operations.updateAccountBalanceMoney(subAccount, token, request.getTransferMoney().toString(), false, "Dépôt pour l'objectif d'épargne: " + goal.getName());

        goal.setUpdatedAt(LocalDateTime.now());
        goalRepository.save(goal);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Montant ajouté avec succès.");
    }

    public ResponseEntity<String> removeGoalMoney (GoalMoneyRequest request, String token, String goalId) {
        SubAccountRole role = jwtService.extractSubAccountRole(token);
        String subAccountIdChild = jwtService.extractSubAccountId(token);

        Goal goal = goalRepository.findByIdAndSubaccountIdChild(goalId, subAccountIdChild);
        SubAccount subAccount = subAccountRepository.findById(goal.getSubaccountIdChild()).orElseThrow();

        if (!SubAccountRole.CHILD.equals(role) && goal.isUseSavingMoney() && (!goal.isActive() || !goal.isDone())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vous n'avez pas les droits");
        }

        if(goal.getProgression().intValue() == 100) {
            goal.setDone(false);
            goal.setActive(true);
        }

        if (request.getTransferMoney().doubleValue() > goal.getDepositStatement().doubleValue()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La somme à retiré ne doit pas dépassé le montant actuelle de l'épargne.");
        }

        Integer depositStatement = Integer.parseInt(String.valueOf(goal.getDepositStatement().doubleValue()));
        Integer transferMoney = Integer.parseInt(String.valueOf(request.getTransferMoney().doubleValue()));

        Number newDepositMoney = Math.subtractExact(depositStatement, transferMoney);

        operations.updateGoalTransactionHistory(goal, DepositType.WITHDRAWAL, request.getTransferMoney(), newDepositMoney);
        goal.setDepositStatement(newDepositMoney);
        operations.updateProgression(goal);
        operations.updateAccountBalanceMoney(subAccount, token, request.getTransferMoney().toString(), true, "Retrait pour l'objectif d'épargne: " + goal.getName());

        goal.setUpdatedAt(LocalDateTime.now());
        goalRepository.save(goal);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Montant retiré avec succès.");
    }

    public ResponseEntity<AuthResponse> confirmUseSavingMoneyOption (String token, String goalId) {
        SubAccountRole role = jwtService.extractSubAccountRole(token);
        String subAccountIdChild = jwtService.extractSubAccountId(token);

        if (!SubAccountRole.CHILD.equals(role)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Goal goal = goalRepository.findByIdAndSubaccountIdChild(goalId, subAccountIdChild);

        if(goal.isDone()) {
            goal.setConfirmsUseSavingMoney(true);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        goal.setUpdatedAt(LocalDateTime.now());
        goalRepository.save(goal);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    public ResponseEntity<String> confirmSavingMoneyTransfer (String token, String goalId) {
        SubAccountRole role = jwtService.extractSubAccountRole(token);
        String subAccountIdChild = jwtService.extractSubAccountId(token);

        if (!SubAccountRole.CHILD.equals(role)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vous n'avez pas les droits");
        }

        Goal goal = goalRepository.findByIdAndSubaccountIdChild(goalId, subAccountIdChild);
        SubAccount subAccount = subAccountRepository.findById(subAccountIdChild).orElseThrow();

        if(goal.isDone() && goal.isConfirmsUseSavingMoney() && goal.getDepositStatement().doubleValue() == goal.getAmount().doubleValue() && goal.getProgression().intValue() == 100) {

            operations.updateAccountBalanceMoney(subAccount, token, goal.getDepositStatement().toString(), true, "Transfer du total de l'argent de l'objectif " + goal.getName() + " dans mon solde.");

        } else {

            if(goal.isDone() == false) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("L'objectif n'est pas encore atteint");
            }

            if(goal.isConfirmsUseSavingMoney() == false) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas encore confirmé l'utilisation de l'argent de l'objectif.");
            }

        }

        goal.setUseSavingMoney(true);
        goal.setUpdatedAt(LocalDateTime.now());
        goalRepository.save(goal);
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Transfert d'argent de l'épargne vers le solde effectué avec succès");
    }
}
