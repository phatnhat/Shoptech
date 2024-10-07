package com.shoptech.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class CustomAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private static final String AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE = "AUTHORIZATION_REQUEST";

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (OAuth2AuthorizationRequest) session.getAttribute(AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE);
        }
        return null;
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequest(request, response);
            return;
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE, authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            OAuth2AuthorizationRequest authorizationRequest = (OAuth2AuthorizationRequest) session.getAttribute(AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE);
            if (authorizationRequest != null) {
                session.removeAttribute(AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE);
                return authorizationRequest;
            }
        }
        return null;
    }


}
