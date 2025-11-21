package moneybuddy.fr.moneybuddy.controller;

import java.util.List;

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

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/subAccount/{subAccountId}")
    public ResponseEntity<List<Transaction>> getTransactions(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable String subAccountId,
        @RequestParam(required = false ) boolean isGoal
    ) {
        String token = authHeader.substring(7);
        return transactionService.getTransactions(token, subAccountId, isGoal);
    }

    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<Transaction>> getSingleGoalTransactions(
        @PathVariable String goalId
    ) {
        return transactionService.getTransactionByGoalId(goalId);
    }
}
