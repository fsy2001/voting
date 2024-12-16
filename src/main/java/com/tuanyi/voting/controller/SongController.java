package com.tuanyi.voting.controller;

import com.tuanyi.voting.model.NominationState;
import com.tuanyi.voting.model.Song;
import com.tuanyi.voting.model.User;
import com.tuanyi.voting.repository.SongRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Controller
public class SongController {
    private final SongRepository songRepository;

    @Value("${voting-deadline}")
    private LocalDateTime votingDeadline;

    public SongController(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @GetMapping("/recommend")
    public String recommendPage() {
        return "user/recommend";
    }

    @GetMapping("/my")
    public ModelAndView myNomineePage(@RequestAttribute("user") User user) {
        var modelAndView = new ModelAndView("user/my");
        modelAndView.addObject("songs", songRepository.getByUserId(user.userId));
        return modelAndView;
    }

    @PostMapping("/api/recommend")
    @ResponseBody
    public String createSongAPI(HttpServletResponse response,
                                @RequestAttribute("user") User user,
                                @RequestParam("name") String name,
                                @RequestParam("artist") String artist,
                                @RequestParam("reason") String reason) {
        var now = LocalDateTime.now();
        if (now.isAfter(votingDeadline)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "投票已结束";
        }

        if (name.length() > 50 || artist.length() > 50 || reason.length() > 100) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "输入的内容过长";
        }

        var song = new Song();
        song.name = name;
        song.artist = artist;
        song.reason = reason;
        song.state = NominationState.PENDING;
        song.votes = 0;
        song.userId = user.userId;

        songRepository.save(song);

        response.setStatus(HttpServletResponse.SC_CREATED);
        return "提名成功";
    }
}
