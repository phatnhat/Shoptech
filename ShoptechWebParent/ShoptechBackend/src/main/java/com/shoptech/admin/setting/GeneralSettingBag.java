package com.shoptech.admin.setting;

import com.shoptech.common.entity.setting.Setting;
import com.shoptech.common.entity.setting.SettingBag;

import java.util.List;

public class GeneralSettingBag extends SettingBag {
    public GeneralSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public void updateCurrencySymbol(String value){
        super.updateValue("CURRENCY_SYMBOL", value);
    }

    public void updateSiteLogo(String value){
        super.updateValue("SITE_LOGO", value);
    }
}
