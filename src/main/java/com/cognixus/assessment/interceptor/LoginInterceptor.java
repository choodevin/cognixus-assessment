package com.cognixus.assessment.interceptor;

import com.cognixus.assessment.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    LoginService loginService;

    public LoginInterceptor(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        boolean isAuthenticated = loginService.checkAuthentication(request);

        if (!isAuthenticated) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return false;
        }

        return true;
    }
}
