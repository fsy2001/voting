package com.tuanyi.voting.controller;

import com.tuanyi.voting.model.NominationState;
import com.tuanyi.voting.model.Vote;
import com.tuanyi.voting.repository.NomineeRepository;
import com.tuanyi.voting.repository.UserRepository;
import com.tuanyi.voting.repository.VoteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Calendar;
import java.util.Date;

@Controller
public class VoteController {
    private final UserRepository userRepository;
    private final NomineeRepository nomineeRepository;
    private final VoteRepository voteRepository;

    public VoteController(UserRepository userRepository, NomineeRepository nomineeRepository, VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.nomineeRepository = nomineeRepository;
        this.voteRepository = voteRepository;
    }

    @GetMapping("/vote")
    public ModelAndView votePage() {
        var nominees = nomineeRepository.getNomineeByStateOrderByVotesDesc(NominationState.APPROVED);

        var modelAndView = new ModelAndView("user/vote");
        modelAndView.addObject("nominees", nominees);
        return modelAndView;
    }

    @GetMapping("/api/vote")
    @ResponseBody
    public String voteAPI(HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestAttribute("userId") String userId,
                          @RequestParam(value = "nominee") String nomineeId) {
        var nominee = nomineeRepository.getNomineeById(Integer.parseInt(nomineeId));

        if (nominee == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "候选人不存在";
        }

        var user = userRepository.findByUserId(userId); // get the latest item

        // Check if the user's last vote was not today
        if (user.lastVote != null) {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(user.lastVote);
            cal2.setTime(new Date());
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            if (!sameDay) {
                // If the user's last vote was not today, refresh the user's remaining votes to 10
                user.leftVotes = 10;
            }
        }

        if (user.leftVotes <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "投票次数已用完";
        }

        int votesToday = voteRepository.countVotesByUserForNomineeOnDate(user.userId, nominee.id, new Date());
        if (votesToday >= 3) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "同一候选人每天只能投三票";
        }

        user.leftVotes -= 1;
        user.lastVote = new Date();

        user = userRepository.save(user);

        Vote vote = new Vote();
        vote.userId = user.userId;
        vote.nomineeId = nominee.id;
        vote.voteTime = new Date();
        vote.ip = request.getHeader("X-Real-IP");
        voteRepository.save(vote);

        nominee.votes += 1;
        nomineeRepository.save(nominee);

        response.setStatus(HttpServletResponse.SC_OK);
        return "投票成功，今日还剩" + (user.leftVotes) + "票";
    }
}
