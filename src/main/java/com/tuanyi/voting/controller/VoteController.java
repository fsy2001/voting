package com.tuanyi.voting.controller;

import com.tuanyi.voting.model.User;
import com.tuanyi.voting.model.Vote;
import com.tuanyi.voting.repository.NomineeRepository;
import com.tuanyi.voting.repository.UserRepository;
import com.tuanyi.voting.repository.VoteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/api/vote")
    @ResponseBody
    public String vote(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "nominee") String nomineeId) {
        var nominee = nomineeRepository.getNomineeById(Integer.parseInt(nomineeId));

        if (nominee == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "候选人不存在";
        }

        var user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "尚未登录";
        }

        user = userRepository.findByUserId(user.getUserId()); // get the latest item

        // Check if the user's last vote was not today
        if (user.getLastVote() != null) {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(user.getLastVote());
            cal2.setTime(new Date());
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            if (!sameDay) {
                // If the user's last vote was not today, refresh the user's remaining votes to 10
                user.setLeftvotes(10);
            }
        }

        var leftVotes = user.getLeftvotes();

        if (leftVotes <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "投票次数已用完";
        }

        // Check if the user has already voted for the same nominee 3 times today
        int votesToday = voteRepository.countVotesByUserForNomineeOnDate(user.getUserId(), nominee.getId(), new Date());
        if (votesToday >= 3) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "同一候选人每天只能投三票";
        }

        // Update the user's remaining votes and last vote time
        user.setLeftvotes(leftVotes - 1);
        user.setLastVote(new Date());

        // Save the updated user to the database
        user = userRepository.save(user);
        request.getSession().setAttribute("user", user);

        // Create a new vote and save it to the database
        Vote vote = new Vote();
        vote.setUserId(user.getUserId());
        vote.setNomineeId(nominee.getId());
        vote.setVoteTime(new Date());
        vote.setIp(request.getRemoteAddr());
        voteRepository.save(vote);

        nominee.setVotes(nominee.getVotes() + 1);
        nomineeRepository.save(nominee);

        response.setStatus(HttpServletResponse.SC_OK);
        return "投票成功，今日还剩" + (user.getLeftvotes()) + "票";
    }
}
