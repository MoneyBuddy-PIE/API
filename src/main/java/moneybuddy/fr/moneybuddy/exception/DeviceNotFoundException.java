/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.exception;

import org.springframework.http.HttpStatus;

public class DeviceNotFoundException extends MoneyBuddyException {
  public DeviceNotFoundException(String deviceId) {
    super(
        String.format("Device non trouvé avec l'id: %s", deviceId),
        HttpStatus.NOT_FOUND,
        "DEVICE_NOT_FOUND");
  }
}
