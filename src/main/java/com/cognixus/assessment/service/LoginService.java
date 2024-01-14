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

    public String getLoginUrl() {
        // get login url for user to login thru google, after success auth with Google, will auto redirect to login redirect
        return "\nPlease access the below URL to continue google login: \n\nhttps://accounts.google.com/o/oauth2/auth?client_id=" + clientId +
                "&redirect_uri=" + redirectUrl + "&response_type=code&scope=openid%20profile%20email\n";
    }

    public ResponseEntity<String> processLogin(String code) {
        try {
            String accessToken = getAccessTokenFromGoogle(code);
            String email = getUserEmailFromGoogle(accessToken);

            Optional<User> userOptional = userRepository.findByEmail(email);
            User user = userOptional.orElseGet(() -> createUser(email));

            LocalDateTime currentDateTime = LocalDateTime.now();
            user.setToken(accessToken);
            user.setTokenExpire(currentDateTime.plusMinutes(loginExpire));

            userRepository.saveAndFlush(user);

            return ResponseEntity.ok("<p>Your access token is : <br/><b>" + accessToken + "</b></p>" +
                    "<p>Please proceed to use the token to access the todo list APIs using command : " +
                    "<br/><b>curl -H \"token: {your_access_token}\" http://localhost:1234/example/api</p></b>" +
                    "<p>Your token will be expiring in " + loginExpire + " minutes");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Something went wrong!!");
        }
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

    public UUID getUserId(String token) throws Exception {
        Optional<User> userOptional = userRepository.findByToken(token);

        if (userOptional.isPresent()) {
            return userOptional.get().getId();
        } else {
            throw new Exception("User not found or token expired");
        }
    }

    private String constructAuthUrl(String code) {
        return "https://oauth2.googleapis.com/token?code=" + code + "&client_id=" + clientId +
                "&client_secret=" + clientSecret + "&redirect_uri=" + redirectUrl + "&grant_type=authorization_code";
    }

    private String extractJsonNode(JsonNode jsonNode) {
        if (jsonNode != null) {
            String value = jsonNode.toString();
            return value.substring(1, value.length() - 1);
        }

        return "";
    }

    private String getAccessTokenFromGoogle(String code) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders authHeader = new HttpHeaders();
        String authUrl = constructAuthUrl(code);

        authHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> httpEntity = new HttpEntity<>(authHeader);

        ResponseEntity<String> authResponse = restTemplate.postForEntity(authUrl, httpEntity, String.class);

        if (!authResponse.getStatusCode().equals(HttpStatus.OK)) {
            throw new Exception("Failed to obtain access token from Google");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode authNode = objectMapper.readTree(authResponse.getBody());

        return extractJsonNode(authNode.get("access_token"));
    }

    private String getUserEmailFromGoogle(String accessToken) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders userInfoHeader = new HttpHeaders();
        String userInfoUrl = "https://www.googleapis.com/oauth2/v1/userinfo";

        userInfoHeader.set("Authorization", "Bearer " + accessToken);
        userInfoHeader.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(userInfoHeader);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, httpEntity, String.class);

        if (!userInfoResponse.getStatusCode().equals(HttpStatus.OK)) {
            throw new Exception("Failed to obtain user info from Google");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode authNode = objectMapper.readTree(userInfoResponse.getBody());

        return extractJsonNode(authNode.get("email"));
    }

    private User createUser(String email) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        return user;
    }
}
