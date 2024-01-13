package com.cognixus.assessment.service;

import com.cognixus.assessment.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class LoginService {
    @Autowired
    private UserRepository userRepository;
    @Value("${google.auth.client-id}")
    private String clientId;
    @Value("${google.auth.redirect-url}")
    private String redirectUrl;
    @Value("${google.auth.client-secret}")
    private String clientSecret;
    @Value("${config.login-expire}")
    private int loginExpire;

    public String getLoginUrl() {
        return "\nPlease access the below URL to continue google login: \n\nhttps://accounts.google.com/o/oauth2/auth?client_id=" + clientId +
                "&redirect_uri=" + redirectUrl + "&response_type=code&scope=openid%20profile%20email\n";
    }

    public ResponseEntity<String> processLogin(String code) throws Exception {
        String accessToken = "";

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders authHeader = new HttpHeaders();
            authHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String authUrl = "https://oauth2.googleapis.com/token?code=" + code + "&client_id=" + clientId +
                    "&client_secret=" + clientSecret + "&redirect_uri=" + redirectUrl + "&grant_type=authorization_code";

            HttpEntity<String> httpEntity = new HttpEntity<>(authHeader);

            ResponseEntity<String> authResponse = restTemplate.postForEntity(authUrl, httpEntity, String.class);

            if (authResponse.getStatusCode().equals(HttpStatus.OK)) {
                log.info("Response from GOOGLE for AUTH -- OK");
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode authNode = objectMapper.readTree(authResponse.getBody());

                accessToken = authNode.get("access_token").toString();
                accessToken = accessToken.substring(1, accessToken.length() - 1);

                String userInfoUrl = "https://www.googleapis.com/oauth2/v1/userinfo";
                HttpHeaders userInfoHeader = new HttpHeaders();
                userInfoHeader.set("Authorization", "Bearer " + accessToken);
                userInfoHeader.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> userInfoEntity = new HttpEntity<>(userInfoHeader);

                ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userInfoEntity, String.class);

                if (userInfoResponse.getStatusCode().equals(HttpStatus.OK)) {
                    log.info("Response from GOOGLE for USERPROFILE -- OK");
                    JsonNode userInfoNode = objectMapper.readTree(userInfoResponse.getBody());

                    String email = userInfoNode.get("email").toString();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("Something went wrong!!");
        }

        return ResponseEntity.ok("<p>Your access token is : <br/><b>" + accessToken + "</b></p>" +
                "<p>Please proceed to use the token to access the todo list APIs using command : " +
                "<br/><b>curl -H \"token: {your_access_token}\" http://localhost:1234/example/api</p></b>" +
                "<p>Your token will be expiring in " + loginExpire + " minutes");
    }
}
