/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import java.io.IOException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.Receipt.Receipt;
import moneybuddy.fr.moneybuddy.dtos.Receipt.SendReceipt;
import moneybuddy.fr.moneybuddy.service.ReceiptOcrService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/receipt")
@RequiredArgsConstructor
public class ReceiptScanController {

  private final ReceiptOcrService receiptOcrService;

  @PostMapping("")
  public ResponseEntity<Receipt> scanReceipt(
      @RequestHeader("Authorization") String authHeader, @Valid @ModelAttribute SendReceipt body)
      throws IOException {

    String token = authHeader.substring(7);
    boolean res = receiptOcrService.updateMoneyAndInsertReceipt(body.getFile(), token);

    return ResponseEntity.status(res ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(null);
  }
}
