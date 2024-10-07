package com.shoptech.oauth;

import com.shoptech.common.entity.AuthenticationType;
import com.shoptech.common.entity.Customer;
import com.shoptech.customer.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomerOauth2User oauth2User = (CustomerOauth2User) authentication.getPrincipal();

        String name = oauth2User.getName();
        String email = oauth2User.getEmail();
        String countryCode = request.getLocale().getCountry();
        String clientName = oauth2User.getClientName();

        Customer customer = customerService.getCustomerByEmail(email);
        AuthenticationType authenticationType = getAuthenticationType(clientName);

        if(customer == null){
            customerService.addNewCustomerUponOAthLogin(name, email, countryCode, authenticationType);
        }else{
            oauth2User.setFullName(customer.getFullName());
            customerService.updateAuthenticationType(customer, authenticationType);
        }

//        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("facebook")
//                .principal(authentication)
//                .attributes(attrs -> {
//                    attrs.put(HttpServletRequest.class.getName(), request);
//                    attrs.put(HttpServletResponse.class.getName(), response);
//                })
//                .build();
//        OAuth2AuthorizedClient authorizedClient = this.authorizedClientManager.authorize(authorizeRequest);
//
//        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
//        System.out.println("accessToken = " + accessToken.getTokenValue());

        super.onAuthenticationSuccess(request, response, authentication);
    }

    public AuthenticationType getAuthenticationType(String clientName){
        if(clientName.equalsIgnoreCase("google")) return AuthenticationType.GOOGLE;
        else if (clientName.equalsIgnoreCase("facebook")) return AuthenticationType.FACEBOOK;
        return AuthenticationType.DATABASE;
    }
}
