package com.tuanyi.voting.controller;

import com.tuanyi.voting.service.IdentificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    private final IdentificationService identificationService;

    public IndexController(IdentificationService identificationService) {
        this.identificationService = identificationService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/uis")
    public String uis(HttpServletRequest request, @RequestParam(value = "code", required = false) String code) {
        if (code == null) {
            return "redirect:https://tac.fudan.edu.cn/oauth2/authorize.act?client_id=fcd2af24-560c-45d4-ba6d-8f01aa8270f6&response_type=code&scope=basic%20mobile&state=weGo123&redirect_uri=https://tuanyi.fudan.edu.cn/uis";
        }

        try {
            var user = identificationService.getUserByCode(code);
            request.getSession().setAttribute("user", user);
            return "redirect:/";
        } catch (Exception e) {
            return "failed";
        }
    }
}
