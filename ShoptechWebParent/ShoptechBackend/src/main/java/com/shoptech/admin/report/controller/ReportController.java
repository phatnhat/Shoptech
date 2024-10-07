package com.shoptech.admin.report.controller;

import com.shoptech.admin.setting.SettingService;
import com.shoptech.admin.utils.OrderUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {
    @Autowired private SettingService settingService;

    @GetMapping("/reports")
    public String viewSalesReportHome(HttpServletRequest request){
        OrderUtil.loadCurrencySetting(request, settingService);
        return "reports/reports";
    }
}
