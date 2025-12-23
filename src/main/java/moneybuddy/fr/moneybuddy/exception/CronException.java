/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class CronException extends MoneyBuddyException {

  public CronException(String message) {
    super(String.format("CRON JOB - %s", message), HttpStatus.NOT_FOUND, "CRON_NOT_FOUND");
  }

  public CronException() {
    super(String.format("Cron job error"), HttpStatus.NOT_FOUND, "CRON_NOT_FOUND");
  }
}
