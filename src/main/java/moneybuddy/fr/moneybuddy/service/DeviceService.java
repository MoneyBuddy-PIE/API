/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.device.CreateDeviceRequest;
import moneybuddy.fr.moneybuddy.exception.DeviceNotFoundException;
import moneybuddy.fr.moneybuddy.model.Device;
import moneybuddy.fr.moneybuddy.repository.DeviceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

  private final DeviceRepository deviceRepository;
  private final JwtService jwtService;

  public void desactivateDevice(String token) {
    String subAccountId = jwtService.extractSubAccountId(token);
    Device device =
        deviceRepository
            .findBySubAccountId(subAccountId)
            .orElseThrow(
                () -> new DeviceNotFoundException(String.format("%s subAccount", subAccountId)));

    device.setActivated(false);
    device.setUpdatedAt(LocalDateTime.now());
    deviceRepository.save(device);
  }

  public void createOrUpdateDevice(String token, CreateDeviceRequest req) {
    String subAccountId = jwtService.extractSubAccountId(token);
    String accountId = jwtService.extractSubAccountAccountId(token);

    Optional<Device> existingDevice = deviceRepository.findBySubAccountId(subAccountId);
    Device device;

    if (existingDevice.isPresent()) {
      device = existingDevice.get();

      device.setUserId(req.getUserId());
      device.setToken(req.getToken());
      device.setPlatform(req.getDevicePlatform());
      device.setActivated(true);
      device.setUpdatedAt(LocalDateTime.now());

    } else {
      device =
          Device.builder()
              .accountId(accountId)
              .subAccountId(subAccountId)
              .token(req.getToken())
              .userId(req.getUserId())
              .platform(req.getDevicePlatform())
              .activated(true)
              .build();
    }

    deviceRepository.save(device);
  }

  public Device getDeviceBySubAccountId(String subAccountId) {
    return deviceRepository
        .findBySubAccountId(subAccountId)
        .orElseThrow(
            () -> new DeviceNotFoundException(String.format("%s subAccount", subAccountId)));
  }
}
