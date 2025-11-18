package moneybuddy.fr.moneybuddy.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.model.Transaction;
import moneybuddy.fr.moneybuddy.service.TransactionService;
import moneybuddy.fr.moneybuddy.model.DepositDetails;
import moneybuddy.fr.moneybuddy.service.GoalTransactionService;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final GoalTransactionService goalTransactionService;

    @GetMapping("/subAccount/{subAccountId}")
    public ResponseEntity<List<Transaction>> getTransactions(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable String subAccountId
    ) {
        String token = authHeader.substring(7);
        return transactionService.getTransactions(token, subAccountId);
    }

    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<DepositDetails>> getSingleGoalTransactions(
        @PathVariable String goalId
    ) {
        return goalTransactionService.getSingleGoalTransactions(goalId);
    }

    @GetMapping("/subAccount/{subAccountId}/goals")
    public ResponseEntity<Page<DepositDetails>> getAllGoalTransactionsBySubAccountId (
        @PathVariable String subAccountId,
        @RequestParam(required = true) String type,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "order") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        if(type.equals( "parent") && subAccountId != null) {
            return goalTransactionService.getAllGoalTransactionsByParentId(subAccountId, page, size, sortBy, sortDir);
        }

        if(type.equals("child") && subAccountId != null) {
            return goalTransactionService.getAllGoalTransactionsByChildId(subAccountId, page, size, sortBy, sortDir);
        }
        
        return null;
    }

}
