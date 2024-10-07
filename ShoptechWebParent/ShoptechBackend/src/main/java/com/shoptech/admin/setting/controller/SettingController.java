package com.shoptech.admin.setting.controller;

import com.shoptech.admin.FileUploadUtil;
import com.shoptech.admin.setting.CurrencyRepository;
import com.shoptech.admin.setting.GeneralSettingBag;
import com.shoptech.admin.setting.SettingService;
import com.shoptech.common.entity.Currency;
import com.shoptech.common.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/settings")
public class SettingController {
    @Autowired
    private SettingService settingService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping
    public String listAll(Model model) {
        List<Setting> listSettings = settingService.listAllSettings();
        List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();

        for (Setting setting : listSettings) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }
        model.addAttribute("listCurrencies", listCurrencies);
        return "settings/settings";
    }

    @PostMapping("/save_general")
    public String saveGeneralSettings(Model model,
                                      @RequestParam("fileImage") MultipartFile multipartFile,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) throws IOException {

        GeneralSettingBag settingBag = settingService.getGeneralSettingBag();

        saveSiteLogo(multipartFile, settingBag);
        saveCurrencySymbol(request, settingBag);

        updateSettingValueFromForm(request, settingBag.list());

        redirectAttributes.addFlashAttribute("message", "General settings have been saved.");

        return "redirect:/settings";
    }

    private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            settingBag.updateSiteLogo(value);

            String uploadDir = "site-logo/";
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
        Long currencyId = Long.parseLong(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyRepository.findById(currencyId);

        if (findByIdResult.isPresent()) {
            Currency currency = findByIdResult.get();
            settingBag.updateCurrencySymbol(currency.getSymbol());
        }
    }

    private void updateSettingValueFromForm(HttpServletRequest request, List<Setting> listSettings){
        for(Setting setting : listSettings){
            String value = request.getParameter(setting.getKey());
            if(value != null){
                setting.setValue(value);
            }
        }
        settingService.saveAll(listSettings);
    }

    @PostMapping("/save_mail_server")
    public String saveMailServerSettings(Model model,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes){

        List<Setting> mailServerSettings = settingService.getMailServerSettings();
        updateSettingValueFromForm(request, mailServerSettings);

        redirectAttributes.addFlashAttribute("message", "Mail server settings have been saved.");

        return "redirect:/settings#mailServer";
    }

    @PostMapping("/save_mail_templates")
    public String saveMailTemplatesSettings(HttpServletRequest request,
                                         RedirectAttributes redirectAttributes){

        List<Setting> mailTemplateSettings = settingService.getMailTemplateSettings();
        updateSettingValueFromForm(request, mailTemplateSettings);

        redirectAttributes.addFlashAttribute("message", "Mail template settings have been saved.");

        return "redirect:/settings#mailTemplates";
    }

    @PostMapping("/save_payment")
    public String savePaymentSettings(HttpServletRequest request,
                                      RedirectAttributes redirectAttributes){

        List<Setting> mailTemplateSettings = settingService.getPaymentSettings();
        updateSettingValueFromForm(request, mailTemplateSettings);

        redirectAttributes.addFlashAttribute("message", "Payment settings have been saved.");

        return "redirect:/settings#payment";
    }
}
