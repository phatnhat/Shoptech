package com.shoptech.checkout;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
public class CheckoutInfo {
    private float productCost;
    private float productTotal;
    private float shippingCostTotal;
    private float paymentTotal;
    private int deliverDays;
    private Date deliverDate;
    private boolean codSupported;

    public Date getDeliverDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, this.deliverDays);
        return calendar.getTime();
    }

    public String getPaymentTotalFormat(){
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        return decimalFormat.format(this.paymentTotal);
    }
}
