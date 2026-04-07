/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.Money.AddMoney;
import moneybuddy.fr.moneybuddy.dtos.Receipt.Receipt;
import moneybuddy.fr.moneybuddy.exception.CustomError;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReceiptOcrService {

  private final JwtService jwtService;
  private final MoneyService moneyService;

  private String OCR_ENDPOINT = "https://ocr.asprise.com/api/v1/receipt";
  HttpClient client = HttpClient.newHttpClient();
  private final ObjectMapper objectMapper;

  public boolean updateMoneyAndInsertReceipt(MultipartFile file, String token) throws IOException {
    String subAccountId = jwtService.extractSubAccountId(token);
    Receipt receipts = scanReceipt(file);
    System.out.println(receipts);

    receipts
        .getReceipts()
        .forEach(
            (receipt) -> {
              System.out.println(receipt);

              if (receipt.getOcr_confidence().compareTo(new BigDecimal(50)) < 0)
                throw new CustomError(
                    String.format(
                        "SubAccount %s | Niveau de confiance insuffisant : %s%%",
                        subAccountId, receipt.getOcr_confidence()),
                    HttpStatus.FORBIDDEN,
                    "ASPRISE_ERROR");

              AddMoney addMoney =
                  AddMoney.builder()
                      .subAccountId(subAccountId)
                      .amount(receipt.getTotal())
                      .description(
                          String.format("%s | %s%", receipt.getMerchant_name(), receipt.getDate()))
                      .build();

              moneyService.updateMoney(addMoney, token, false);
            });

    return receipts.isSuccess();
  }

  public Receipt scanReceipt(MultipartFile file) throws IOException {
    String boundary = "Boundary" + System.currentTimeMillis();

    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(OCR_ENDPOINT))
              .header("Content-Type", "multipart/form-data; boundary=" + boundary)
              .POST(HttpRequest.BodyPublishers.ofByteArray(buildBody(boundary, file)))
              .build();

      HttpResponse<String> response =
          client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

      Receipt receipt = objectMapper.readValue(response.body(), Receipt.class);

      if (!receipt.isSuccess())
        throw new CustomError(receipt.getMessage(), HttpStatus.FORBIDDEN, "ASPRISE_ERROR");

      return receipt;
    } catch (Exception e) {
      throw new CustomError(e.getMessage(), HttpStatus.FORBIDDEN, "ASPRISE_ERROR");
    }
  }

  private byte[] buildBody(String boundary, MultipartFile file) throws IOException {
    String CRLF = "\r\n";
    String dashes = "--" + boundary;

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    // api_key
    write(
        out,
        dashes
            + CRLF
            + "Content-Disposition: form-data; name=\"api_key\""
            + CRLF
            + CRLF
            + "TEST"
            + CRLF); // remplace TEST par ta vraie clé en prod

    // recognizer
    write(
        out,
        dashes
            + CRLF
            + "Content-Disposition: form-data; name=\"recognizer\""
            + CRLF
            + CRLF
            + "auto"
            + CRLF); // 'auto' détecte la langue, ou force "FR"

    // ref_no
    write(
        out,
        dashes
            + CRLF
            + "Content-Disposition: form-data; name=\"ref_no\""
            + CRLF
            + CRLF
            + "moneybuddy_"
            + System.currentTimeMillis()
            + CRLF);

    // file
    String fileName =
        file.getOriginalFilename() != null
            ? file.getOriginalFilename().replace(" ", "_")
            : "receipt.png";

    write(
        out,
        dashes
            + CRLF
            + "Content-Disposition: form-data; name=\"file\"; filename=\""
            + fileName
            + "\""
            + CRLF
            + "Content-Type: application/octet-stream"
            + CRLF
            + CRLF); // ← ici le fix

    out.write(file.getBytes());
    write(out, CRLF);

    // Fermeture
    write(out, dashes + "--" + CRLF);

    return out.toByteArray();
  }

  private void write(ByteArrayOutputStream out, String text) throws IOException {
    out.write(text.getBytes());
  }
}
