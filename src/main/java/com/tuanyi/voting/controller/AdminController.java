package com.tuanyi.voting.controller;

import com.tuanyi.voting.model.NominationState;
import com.tuanyi.voting.repository.NomineeRepository;
import com.tuanyi.voting.repository.UserRepository;
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

    @GetMapping("/admin")
    public ModelAndView home() {
        var modelAndView = new ModelAndView("admin-home");
        var allNominees = nomineeRepository.findAllByOrderByIdDesc();
        modelAndView.addObject("nominees", allNominees);
        return modelAndView;
    }

    @GetMapping("/api/admin/approve-nominee")
    @ResponseBody
    public String approveNomineeAPI(HttpServletResponse response,
                                    @RequestParam(value = "approve") Boolean approve,
                                    @RequestParam(value = "id") Integer nomineeId,
                                    @RequestParam(value = "reason", required = false) String rejectReason) {
        var nominee = nomineeRepository.getNomineeById(nomineeId);
        if (nominee == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "候选人不存在";
        }

        if (approve) {
            nominee.state = NominationState.APPROVED;
            nominee.rejectReason = null;
        } else {
            nominee.state = NominationState.REJECTED;
            nominee.rejectReason = rejectReason;
        }

        nomineeRepository.save(nominee);
        return "成功";
    }

    @GetMapping("/api/admin/create-admin")
    @ResponseBody
    public String addAdminAPI(HttpServletResponse response,
                              @RequestParam(value = "userId") String userId) {
        var user = userRepository.findByUserId(userId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "用户不存在";
        }
        user.isAdmin = true;
        userRepository.save(user);
        return "成功";
    }

    @GetMapping("/api/admin/change-vote")
    @ResponseBody
    public String changeVoteAPI(HttpServletResponse response,
                                @RequestParam(value = "nomineeId") Integer nomineeId,
                                @RequestParam(value = "newVote") Integer newVote) {
        var nominee = nomineeRepository.getNomineeById(nomineeId);
        if (nominee == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "候选人不存在";
        }

        nominee.votes = newVote;
        nomineeRepository.save(nominee);
        return "成功";
    }
}