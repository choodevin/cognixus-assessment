package com.cognixus.assessment.config;

import com.cognixus.assessment.interceptor.LoginInterceptor;
import com.cognixus.assessment.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginService loginService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginInterceptor myInterceptor = new LoginInterceptor(loginService);

        registry.addInterceptor(myInterceptor)
                .excludePathPatterns("/login", "/login/**")
                .addPathPatterns("/**");
    }
}
