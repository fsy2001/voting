package com.tuanyi.voting.controller;

import com.tuanyi.voting.model.NominationState;
import com.tuanyi.voting.model.Vote;
import com.tuanyi.voting.repository.SongRepository;
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

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public AdminController(SongRepository songRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    @GetMapping("/admin")
    public ModelAndView homePage() {
        var modelAndView = new ModelAndView("admin/home");
        var allSongs = songRepository.findAllByOrderByIdDesc();
        modelAndView.addObject("songs", allSongs);
        return modelAndView;
    }

    @GetMapping("/api/admin/approve-song")
    @ResponseBody
    public String approveSongAPI(HttpServletResponse response,
                                 @RequestParam(value = "approve") Boolean approve,
                                 @RequestParam(value = "songId") Integer songId,
                                 @RequestParam(value = "reason", required = false) String rejectReason) {
        var song = songRepository.getSongById(songId);
        if (song == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "歌曲不存在";
        }

        if (approve) {
            song.state = NominationState.APPROVED;
            song.rejectReason = null;
        } else {
            song.state = NominationState.REJECTED;
            song.rejectReason = rejectReason;
        }

        songRepository.save(song);
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
                                @RequestParam(value = "songId") Integer songId,
                                @RequestParam(value = "newVote") Integer newVote) {
        var nominee = songRepository.getSongById(songId);
        if (nominee == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "歌曲不存在";
        }

        nominee.votes = newVote;
        songRepository.save(nominee);
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        return "成功";
    }

    @GetMapping("/admin/vote")
    public ModelAndView voteInspectionPage(@RequestParam(value = "id") Integer songId) {
        var song = songRepository.getSongById(songId);
        var votes = voteRepository.findAllBySongIdOrderByVoteTimeDesc(songId);
        var modelAndView = new ModelAndView("admin/vote");
        modelAndView.addObject("votes", votes);
        modelAndView.addObject("nominee", song);
        return modelAndView;
    }

    @GetMapping("/api/admin/vote")
    @ResponseBody
    public Map<String, List<?>> voteInspectionAPI(@RequestParam(value = "songId") Integer songId) {
        List<Vote> votes = voteRepository.findAllBySongId(songId);

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