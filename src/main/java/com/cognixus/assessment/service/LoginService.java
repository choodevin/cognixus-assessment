package com.cognixus.assessment.service;

import com.cognixus.assessment.model.entity.User;
import com.cognixus.assessment.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
    private String authUrl = "https://oauth2.googleapis.com/token?code=";

    public String getLoginUrl() {
        // get login url for user to login thru google
        return "\nPlease access the below URL to continue google login: \n\nhttps://accounts.google.com/o/oauth2/auth?client_id=" + clientId +
                "&redirect_uri=" + redirectUrl + "&response_type=code&scope=openid%20profile%20email\n";
    }

    public ResponseEntity<String> processLogin(String code) throws Exception {
        String accessToken = "";

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders authHeader = new HttpHeaders();
            authHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            authUrl = constructAuthUrl(code);

            HttpEntity<String> httpEntity = new HttpEntity<>(authHeader);

            // get access token from google
            ResponseEntity<String> authResponse = restTemplate.postForEntity(authUrl, httpEntity, String.class);

            if (authResponse.getStatusCode().equals(HttpStatus.OK)) {
                log.info("Response from GOOGLE for AUTH -- OK");
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode authNode = objectMapper.readTree(authResponse.getBody());

                accessToken = extractJsonNode(authNode.get("access_token"));

                HttpHeaders userInfoHeader = new HttpHeaders();
                userInfoHeader.set("Authorization", "Bearer " + accessToken);
                userInfoHeader.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> userInfoEntity = new HttpEntity<>(userInfoHeader);

                // get user info from google
                String userInfoUrl = "https://www.googleapis.com/oauth2/v1/userinfo";
                ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userInfoEntity, String.class);

                if (userInfoResponse.getStatusCode().equals(HttpStatus.OK)) {
                    log.info("Response from GOOGLE for USERPROFILE -- OK");
                    JsonNode userInfoNode = objectMapper.readTree(userInfoResponse.getBody());

                    String email = extractJsonNode(userInfoNode.get("email"));
                    Optional<User> userOptional = userRepository.findByEmail(email);

                    // process user login using email from google
                    User user;
                    if (userOptional.isPresent()) {
                        user = userOptional.get();
                    } else {
                        user = new User();
                        user.setId(UUID.randomUUID());
                        user.setEmail(email);
                    }

                    LocalDateTime currentDateTime = LocalDateTime.now();
                    user.setToken(accessToken);
                    user.setTokenExpire(currentDateTime.plusMinutes(loginExpire));

                    userRepository.saveAndFlush(user);
                }
            }
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Something went wrong!!");
        }

        return ResponseEntity.ok("<p>Your access token is : <br/><b>" + accessToken + "</b></p>" +
                "<p>Please proceed to use the token to access the todo list APIs using command : " +
                "<br/><b>curl -H \"token: {your_access_token}\" http://localhost:1234/example/api</p></b>" +
                "<p>Your token will be expiring in " + loginExpire + " minutes");
    }

    public boolean checkAuthentication(HttpServletRequest request) {
        String token = request.getHeader("token");
        // find user using token, then verify token expiry
        if (token != null) {
            Optional<User> userOptional = userRepository.findByToken(token);
            if (userOptional.isPresent()) {
                LocalDateTime tokenExpireTime = userOptional.get().getTokenExpire();
                LocalDateTime currentDateTime = LocalDateTime.now();

                return currentDateTime.isBefore(tokenExpireTime);
            }
        }

        return false;
    }

    private String constructAuthUrl(String code) {
        return authUrl + code + "&client_id=" + clientId +
                "&client_secret=" + clientSecret + "&redirect_uri=" + redirectUrl + "&grant_type=authorization_code";
    }

    private String extractJsonNode(JsonNode jsonNode) {
        String value = jsonNode.toString();
        return value.substring(1, value.length() - 1);
    }

    public UUID getUserId(String token) throws Exception {
        Optional<User> userOptional = userRepository.findByToken(token);

        if (userOptional.isPresent()) {
            return userOptional.get().getId();
        } else {
            throw new Exception("User not found or token expired");
        }
    }
}
