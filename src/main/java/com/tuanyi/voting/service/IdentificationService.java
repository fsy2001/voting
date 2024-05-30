package com.tuanyi.voting.service;

import com.tuanyi.voting.model.User;
import com.tuanyi.voting.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

@Service
public class IdentificationService {
    private final HttpClient httpClient;
    private final UserRepository userRepository;

    public IdentificationService(HttpClient httpClient, UserRepository userRepository) {
        this.httpClient = httpClient;
        this.userRepository = userRepository;
    }

    public User getUserByCode(String code) throws IOException, InterruptedException {
        var oauthURL = "https://tac.fudan.edu.cn/oauth2/token.act?client_id=fcd2af24-560c-45d4-ba6d-8f01aa8270f6&client_secret=HvpcxuzhuBBft32CmL8D&grant_type=authorization_code&redirect_uri=https://tuanyi.fudan.edu.cn/uis&code=" + code;
        var oauthResponse = request(oauthURL);
        var token = parse(oauthResponse, "access_token");

        var userInfoResponse = request("https://tac.fudan.edu.cn/resource/userinfo.act?access_token=" + token);
        var username = parse(userInfoResponse, "user_name");
        var userId = parse(userInfoResponse, "user_id");

        var existingUser = userRepository.findByUserId(userId);
        if (existingUser != null) {
            return existingUser;
        }

        var user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setLeftvotes(10);
        user = userRepository.save(user);
        return user;
    }

    private HttpResponse<String> request(String url) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String parse(HttpResponse<String> response, String key) {
        var json = new JSONObject(response.body());
        return json.getString(key);
    }
}