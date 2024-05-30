package com.tuanyi.voting.controller;

import com.tuanyi.voting.model.NominationState;
import com.tuanyi.voting.model.User;
import com.tuanyi.voting.repository.NomineeRepository;
import com.tuanyi.voting.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

    private final NomineeRepository nomineeRepository;
    private final UserRepository userRepository;

    public AdminController(NomineeRepository nomineeRepository, UserRepository userRepository) {
        this.nomineeRepository = nomineeRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/admin/nominee")
    public ModelAndView adminNominee(HttpServletRequest request) {
        var session = request.getSession();
        var user = (User) session.getAttribute("user");


        if (user == null) {
            return new ModelAndView("redirect:/uis");
        }

        user = userRepository.findByUserId(user.getUserId());
        var isAdmin = user.isAdmin();

        if (isAdmin == null || !isAdmin) {
            return new ModelAndView("redirect:/");
        }

        var modelAndView = new ModelAndView("admin-nominee");
        var allNominees = nomineeRepository.findAllByOrderByIdDesc();
        modelAndView.addObject("nominees", allNominees);
        return modelAndView;
    }

    @GetMapping("/api/admin/nominee")
    @ResponseBody
    public String adminSetNominee(HttpServletRequest request,
                                  HttpServletResponse response,
                                  @RequestParam(value = "approve") Boolean approve,
                                  @RequestParam(value = "id") Integer nomineeId) {
        var session = request.getSession();
        var user = (User) session.getAttribute("user");

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "没有登录";
        }

        user = userRepository.findByUserId(user.getUserId());
        var isAdmin = user.isAdmin();

        if (isAdmin == null || !isAdmin) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "没有权限";
        }

        var nominee = nomineeRepository.getNomineeById(nomineeId);
        if (nominee == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "候选人不存在";
        }

        if (approve) {
            nominee.setState(NominationState.APPROVED);
        } else {
            nominee.setState(NominationState.REJECTED);
        }

        nomineeRepository.save(nominee);
        return "成功";
    }

    @GetMapping("/admin/set")
    @ResponseBody
    public String setAdmin(HttpServletRequest request,
                           HttpServletResponse response,
                           @RequestParam(value = "userId") String userId) {
        var session = request.getSession();
        var user = (User) session.getAttribute("user");


        if (user == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "没有登录";
        }

        user = userRepository.findByUserId(user.getUserId());
        var isAdmin = user.isAdmin();

        if (isAdmin == null || !isAdmin) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "没有权限";
        }

        var admin = userRepository.findByUserId(userId);
        if (admin == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "用户不存在";
        }
        admin.setAdmin(true);
        userRepository.save(admin);
        return "成功";
    }

    @GetMapping("/admin/adjust")
    public String adjustNominee(HttpServletRequest request) {
        var session = request.getSession();
        var user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/uis";
        }

        user = userRepository.findByUserId(user.getUserId());
        var isAdmin = user.isAdmin();

        if (isAdmin == null || !isAdmin) {
            return "redirect:/";
        }

        return "admin-adjust";
    }

    @GetMapping("/api/admin/adjust")
    @ResponseBody
    public String adjustNominee(HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestParam(value = "nomineeId") Integer nomineeId,
                                @RequestParam(value = "newVote") Integer newVote) {
        var session = request.getSession();
        var user = (User) session.getAttribute("user");

        if (user == null) {
            return "没有登录";
        }

        user = userRepository.findByUserId(user.getUserId());
        var isAdmin = user.isAdmin();

        if (isAdmin == null || !isAdmin) {
            return "没有权限";
        }

        var nominee = nomineeRepository.getNomineeById(nomineeId);
        if (nominee == null) {
            return "候选人不存在";
        }

        nominee.setVotes(newVote);
        nomineeRepository.save(nominee);
        return "成功";
    }
}
