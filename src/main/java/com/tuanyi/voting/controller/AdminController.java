package com.tuanyi.voting.controller;

import com.tuanyi.voting.model.NominationState;
import com.tuanyi.voting.model.Vote;
import com.tuanyi.voting.repository.NomineeRepository;
import com.tuanyi.voting.repository.UserRepository;
import com.tuanyi.voting.repository.VoteRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final NomineeRepository nomineeRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public AdminController(NomineeRepository nomineeRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.nomineeRepository = nomineeRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    @GetMapping("/admin")
    public ModelAndView homePage() {
        var modelAndView = new ModelAndView("admin/home");
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
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
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
        response.setStatus(HttpServletResponse.SC_CREATED);
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
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        return "成功";
    }

    @GetMapping("/admin/vote")
    public ModelAndView voteInspectionPage(@RequestParam(value = "id") Integer nomineeId) {
        var nominee = nomineeRepository.getNomineeById(nomineeId);
        var votes = voteRepository.findAllByNomineeIdOrderByVoteTimeDesc(nomineeId);
        var modelAndView = new ModelAndView("admin/vote");
        modelAndView.addObject("votes", votes);
        modelAndView.addObject("nominee", nominee);
        return modelAndView;
    }

    @GetMapping("/api/admin/vote")
    @ResponseBody
    public Map<String, List<?>> voteInspectionAPI(@RequestParam(value = "id") Integer nomineeId) {
        List<Vote> votes = voteRepository.findAllByNomineeId(nomineeId);

        var grouped = votes.stream()
                .collect(Collectors.groupingBy(
                        vote -> vote.voteTime.truncatedTo(ChronoUnit.HOURS),
                        Collectors.counting()
                ));

        var sorted = new TreeMap<>(grouped);

        List<String> timeList = new ArrayList<>();
        List<Long> votesList = new ArrayList<>();
        var formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");

        for (var time: sorted.keySet()) {
            var timeString = formatter.format(time);
            timeList.add(timeString);
            votesList.add(sorted.get(time));
        }

        Map<String, List<?>> result = new HashMap<>();
        result.put("time", timeList);
        result.put("votes", votesList);
        return result;
    }
}