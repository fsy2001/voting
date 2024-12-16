package com.tuanyi.voting.controller;

import com.tuanyi.voting.service.IdentificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class IndexController {
    private final IdentificationService identificationService;

    @Value("${oauth.client-id}")
    private String clientId;

    public IndexController(IdentificationService identificationService) {
        this.identificationService = identificationService;
    }

    @GetMapping("/")
    public String index() {
        return "user/welcome";
    }

    @GetMapping("/forbidden")
    public String forbidden() {
        return "exception/forbidden";
    }

    @GetMapping("/uis")
    public String uis(HttpServletRequest request,
                      @RequestParam(value = "code", required = false) String code,
                      @RequestParam(value = "from", required = false) String from) {
        if (code == null) {
            var redirectURI = UriComponentsBuilder
                    .fromUriString("https://tac.fudan.edu.cn/oauth2/authorize.act")
                    .queryParam("client_id", clientId)
                    .queryParam("response_type", "code")
                    .queryParam("scope", "basic mobile")
                    .queryParam("state", "weGo123")
                    .queryParam("redirect_uri", "https://tuanyi.fudan.edu.cn/uis")
                    .build().toString();
            return "redirect:" + redirectURI;
        }

        try {
            var user = identificationService.getUserByCode(code);
            request.getSession().setAttribute("user", user);
            if (from != null) {
                return "redirect:" + from;
            }
            return "redirect:/";
        } catch (Exception e) {
            return "exception/login-failed";
        }
    }
}
