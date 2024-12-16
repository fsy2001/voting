package com.tuanyi.voting.service;

import com.tuanyi.voting.model.User;
import com.tuanyi.voting.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class IdentificationService {
    @Value("${oauth.client-id}")
    private String clientId;

    @Value("${oauth.client-secret}")
    private String clientSecret;

    private final HttpClient httpClient;
    private final UserRepository userRepository;

    public IdentificationService(HttpClient httpClient, UserRepository userRepository) {
        this.httpClient = httpClient;
        this.userRepository = userRepository;
    }


    public User getUserByCode(String code) throws IOException, InterruptedException {
        var oauthURI = UriComponentsBuilder
                .fromUriString("https://tac.fudan.edu.cn/oauth2/token.act")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("grant_type", "authorization_code")
                .queryParam("redirect_uri", "https://tuanyi.fudan.edu.cn/uis")
                .queryParam("code", code)
                .build().toUri();
        var oauthResponse = request(oauthURI);
        var token = parse(oauthResponse, "access_token");

        var resourceURI = UriComponentsBuilder
                .fromUriString("https://tac.fudan.edu.cn/resource/userinfo.act")
                .queryParam("access_token", token)
                .build().toUri();
        var userInfoResponse = request(resourceURI);
        var username = parse(userInfoResponse, "user_name");
        var userId = parse(userInfoResponse, "user_id");

        var existingUser = userRepository.findByUserId(userId);
        if (existingUser != null) {
            return existingUser;
        }

        var user = new User();
        user.userId = userId;
        user.username = username;
        user.leftVotes = 10;
        user.isAdmin = false;
        user = userRepository.save(user);
        return user;
    }

    private HttpResponse<String> request(URI uri) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder().uri(uri).build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String parse(HttpResponse<String> response, String key) {
        var json = new JSONObject(response.body());
        return json.getString(key);
    }
}