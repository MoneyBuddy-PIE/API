/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class CloudFlareNotSupportedFileType extends MoneyBuddyException {
  public CloudFlareNotSupportedFileType(String fileType) {
    super(
        String.format("Not supported file type for chapter, type %s : ", fileType),
        HttpStatus.NOT_FOUND,
        "CLOUDFLARE_NOT_FOUND");
  }
}
