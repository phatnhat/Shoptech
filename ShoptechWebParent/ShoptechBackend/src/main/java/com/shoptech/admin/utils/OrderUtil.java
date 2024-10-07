package com.shoptech.admin.utils;

import com.shoptech.admin.setting.SettingService;
import com.shoptech.common.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public class OrderUtil {
    public static void loadCurrencySetting(HttpServletRequest request, SettingService settingService) {
        List<Setting> currencySettings = settingService.getCurrencySettings();

        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }

}
