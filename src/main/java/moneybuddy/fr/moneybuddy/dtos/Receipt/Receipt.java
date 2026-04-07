/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.Receipt;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Receipt {

  // API status
  @JsonProperty("message")
  String message;

  @JsonProperty("success")
  boolean success;

  // Data
  @JsonProperty("ocr_type")
  String ocr_type;

  @JsonProperty("request_id")
  String request_id;

  @JsonProperty("receipts")
  List<ReceiptRow> receipts;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ReceiptRow {
    @JsonProperty("ocr_confidence")
    BigDecimal ocr_confidence;

    @JsonProperty("merchant_name")
    String merchant_name;

    @JsonProperty("date")
    String date;

    @JsonProperty("currency")
    String currency;

    @JsonProperty("total")
    BigDecimal total;

    @JsonProperty("ocr_text")
    String ocr_text;

    @JsonProperty("items")
    List<ReceiptItem> items;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReceiptItem {
      @JsonProperty("amount")
      BigDecimal amount;

      @JsonProperty("description")
      String description;
    }
  }
}
