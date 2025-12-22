/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.Income.CollectAllIncome;
import moneybuddy.fr.moneybuddy.dtos.Income.UpdateIncomeRequest;
import moneybuddy.fr.moneybuddy.dtos.ResponseDto;
import moneybuddy.fr.moneybuddy.model.Income;
import moneybuddy.fr.moneybuddy.model.enums.IncomeStatus;
import moneybuddy.fr.moneybuddy.service.IncomeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/incomes")
@RequiredArgsConstructor
public class IncomeController {

  private final IncomeService incomeService;

  @GetMapping("")
  public ResponseEntity<List<Income>> getAllIncomes(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(required = false) String childId,
      @RequestParam(required = false) String parentId,
      @RequestParam(required = false) IncomeStatus status) {
    String token = authHeader.substring(7);
    List<Income> incomes = incomeService.getAllIncomes(token, childId, parentId, status);
    return ResponseEntity.status(HttpStatus.OK).body(incomes);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Income> getIncome(@PathVariable String id) {
    return ResponseEntity.status(HttpStatus.OK).body(incomeService.getIncome(id));
  }

  @PostMapping("/send")
  public ResponseEntity<ResponseDto> collectAllIncome(
      @RequestHeader("Authorization") String authHeader, @Valid @RequestBody CollectAllIncome req) {
    String token = authHeader.substring(7);

    incomeService.collectAllIncome(token, req);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDto.builder().message("Revenue convertie en argent").build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ResponseDto> deleteIncome(@PathVariable String id) {
    incomeService.deleteIncome(id);

    ResponseDto res = ResponseDto.builder().message("Supprimer avec succes").build();
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(res);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ResponseDto> updateIncomeStatus(
      @RequestHeader("Authorization") String authHeader,
      @PathVariable String id,
      @Valid @RequestBody UpdateIncomeRequest req) {
    String token = authHeader.substring(7);
    incomeService.updateIncome(token, id, req);

    ResponseDto res = ResponseDto.builder().message("Revenue modifié").build();
    return ResponseEntity.status(HttpStatus.OK).body(res);
  }
}
