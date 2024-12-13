package com.tuanyi.voting.controller;

import com.tuanyi.voting.model.NominationState;
import com.tuanyi.voting.model.Vote;
import com.tuanyi.voting.repository.SongRepository;
import com.tuanyi.voting.repository.UserRepository;
import com.tuanyi.voting.repository.VoteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

@Controller
public class VoteController {
    private final ReentrantLock voteLock = new ReentrantLock();
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final VoteRepository voteRepository;

    @Value("${voting-deadline}")
    private LocalDateTime votingDeadline;

    @Value("${votes-visible-until}")
    private LocalDateTime votesVisibleDeadline;

    public VoteController(UserRepository userRepository, SongRepository songRepository, VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.voteRepository = voteRepository;
    }

    @GetMapping("/vote")
    public ModelAndView votePage() {
        var now = LocalDateTime.now();
        var votesVisible = now.isBefore(votesVisibleDeadline);
        var songs = votesVisible ? songRepository.getByStateOrderByVotesDesc(NominationState.APPROVED) : songRepository.getByStateOrderByIdDesc(NominationState.APPROVED);
        var modelAndView = new ModelAndView("user/vote");
        modelAndView.addObject("songs", songs);
        modelAndView.addObject("votesVisible", votesVisible);
        return modelAndView;
    }

    @GetMapping("/api/vote")
    @ResponseBody
    @Transactional
    public synchronized String voteAPI(HttpServletRequest request,
                                       HttpServletResponse response,
                                       @RequestAttribute("userId") String userId,
                                       @RequestParam(value = "songId") String songId) {
        var now = LocalDateTime.now();
        var today = now.toLocalDate();

        if (now.isAfter(votingDeadline)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "投票已结束";
        }

        voteLock.lock(); // prevent concurrent execution
        try {
            var song = songRepository.getSongById(Integer.parseInt(songId));

            if (song == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "候选人不存在";
            }

            var user = userRepository.findByUserId(userId);
            var lastVote = user.lastVote;

            // refresh left vote count on a new day
            if (lastVote != null) {
                var lastVoteDate = lastVote.toLocalDate();
                var sameDay = lastVoteDate.equals(today);
                if (!sameDay) {
                    user.leftVotes = 10;
                }
            }

            if (user.leftVotes <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "投票次数已用完";
            }

            var allVotes = voteRepository.findAllByUserIdAndSongId(user.userId, song.id);
            var voteCount = allVotes
                    .stream()
                    .filter(vote -> vote.voteTime.toLocalDate().equals(today))
                    .count();

            if (voteCount >= 3) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "同一候选人每天只能投三票";
            }

            user.leftVotes -= 1;
            user.lastVote = LocalDateTime.now();

            user = userRepository.save(user);

            var vote = new Vote();
            vote.userId = user.userId;
            vote.songId = song.id;
            vote.voteTime = now;
            vote.ip = request.getHeader("X-Real-IP");
            voteRepository.save(vote);

            song.votes += 1;
            songRepository.save(song);

            response.setStatus(HttpServletResponse.SC_CREATED);
            return "投票成功，今日还剩" + (user.leftVotes) + "票";
        } finally {
            voteLock.unlock();
        }
    }
}
