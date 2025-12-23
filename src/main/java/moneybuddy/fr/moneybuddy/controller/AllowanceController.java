/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.Allowance.CreateAllowance;
import moneybuddy.fr.moneybuddy.dtos.Allowance.UpdateAllowance;
import moneybuddy.fr.moneybuddy.model.Allowance;
import moneybuddy.fr.moneybuddy.service.AllowanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/allowance")
@RequiredArgsConstructor
public class AllowanceController {

  private final AllowanceService allowanceService;

  @PostMapping("")
  public ResponseEntity<Allowance> createAllowance(
      @RequestHeader("Authorization") String authHeader, @Valid @RequestBody CreateAllowance req) {
    String token = authHeader.substring(7);
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(allowanceService.createAllowance(req, token));
  }

  @GetMapping("")
  public ResponseEntity<List<Allowance>> getAll(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    return ResponseEntity.status(HttpStatus.OK).body(allowanceService.getAll(token));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Allowance> getbyId(@RequestParam String id) {
    return ResponseEntity.status(HttpStatus.OK).body(allowanceService.getById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Allowance> updateAllowance(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam String id,
      @Valid @RequestBody UpdateAllowance req) {
    String token = authHeader.substring(7);
    return ResponseEntity.status(HttpStatus.OK)
        .body(allowanceService.updateAllowance(req, id, token));
  }
}
