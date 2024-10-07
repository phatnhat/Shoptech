package com.shoptech.setting;

import com.shoptech.common.entity.setting.Setting;
import com.shoptech.common.entity.setting.SettingBag;

import java.util.List;

public class PaymentSettingBag extends SettingBag {
    public PaymentSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getPaypalURL(){
        return super.getValue("PAYPAL_API_BASE_URL");
    }

    public String getPaypalClientID(){
        return super.getValue("PAYPAL_API_CLIENT_ID");
    }

    public String getPaypalClientSecret(){
        return super.getValue("PAYPAL_API_CLIENT_SECRET");
    }

    public String getVnpayApiUrl(){
        return super.getValue("VNPAY_API_URL");
    }

    public String getVnpayPayUrl(){
        return super.getValue("VNPAY_PAY_URL");
    }

    public String getVnpayTmnCode(){
        return super.getValue("VNPAY_TMN_CODE");
    }

    public String getVnpayHashSecret(){
        return super.getValue("VNPAY_HASH_SECRET");
    }
}
