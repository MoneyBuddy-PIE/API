/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.setting.UpdateSetting;
import moneybuddy.fr.moneybuddy.exception.SettingNotFound;
import moneybuddy.fr.moneybuddy.model.Setting;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.repository.SettingRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingService {

  private final SettingRepository settingRepository;

  public Setting createSetting(SubAccount subAccount) {
    Setting setting = Setting.builder().subAccountId(subAccount.getId()).build();
    return settingRepository.save(setting);
  }

  public Setting getSetting(String id) {
    Setting setting = settingRepository.findById(id).orElseThrow(() -> new SettingNotFound(id));
    return setting;
  }

  public Setting getSettingBySubAccountId(String id) {
    Setting setting =
        settingRepository
            .findBySubAccountId(id)
            .orElseThrow(() -> new SettingNotFound(id + " sous-compt"));
    return setting;
  }

  public Setting modifySetting(String id, UpdateSetting req) {
    Setting setting = getSetting(id);

    setting.setPreValidate(
        Optional.ofNullable(req.isPreValidate()).orElse(setting.isPreValidate()));

    Setting newSetting = settingRepository.save(setting);
    return newSetting;
  }
}
