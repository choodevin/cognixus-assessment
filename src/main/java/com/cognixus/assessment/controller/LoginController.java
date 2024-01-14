package com.cognixus.assessment.controller;

import com.cognixus.assessment.constants.ResourcePath;
import com.cognixus.assessment.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping(ResourcePath.LOGIN_ENTRY)
public class LoginController {
    @Autowired
    private LoginService loginService;

    @GetMapping(value = ResourcePath.TRIGGER_LOGIN)
    public String triggerLogin() {
        return loginService.getLoginUrl();
    }

    @GetMapping(value = ResourcePath.LOGIN_REDIRECT)
    public ResponseEntity<String> loginRedirect(@RequestParam String code) {
        return loginService.processLogin(code);
    }
}
