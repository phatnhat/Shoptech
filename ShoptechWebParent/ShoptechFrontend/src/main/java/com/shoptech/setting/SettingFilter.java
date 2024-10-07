package com.shoptech.setting;

import com.shoptech.common.entity.setting.Setting;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SettingFilter implements Filter {
    private static final Set<String> NOT_ALLOWED_EXTENSIONS =
            new HashSet<>(Arrays.asList(".html", ".jsp", ".js", ".css", ".jpg", ".png"));

    @Autowired
    private SettingService settingService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String url = servletRequest.getRequestURL().toString();

        List<Setting> generalSettings = settingService.getGeneralSettingBag();

        if(!hasNotAllowedExtension(url)){
            generalSettings.forEach(setting -> {
                request.setAttribute(setting.getKey(), setting.getValue());
            });
        }else{
            chain.doFilter(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean hasNotAllowedExtension(String url) {
        try{
            String extension = url.substring(url.lastIndexOf('.')).toLowerCase();
            return NOT_ALLOWED_EXTENSIONS.contains(extension);
        }catch (Exception e){
            return false;
        }
    }

}
