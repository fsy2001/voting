package com.tuanyi.voting.controller;

import com.tuanyi.voting.model.NominationState;
import com.tuanyi.voting.model.Song;
import com.tuanyi.voting.model.User;
import com.tuanyi.voting.repository.SongRepository;
import com.tuanyi.voting.service.ImageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Controller
public class SongController {
    private final SongRepository songRepository;
    private final ImageService imageService;

    @Value("${voting-deadline}")
    private LocalDateTime votingDeadline;

    public SongController(SongRepository songRepository, ImageService imageService) {
        this.songRepository = songRepository;
        this.imageService = imageService;
    }

    @GetMapping("/recommend")
    public String recommendPage() {
        return "user/recommend";
    }

    @GetMapping("/nominee")
    public ModelAndView myNomineePage(@RequestAttribute("user") User user) {
        var modelAndView = new ModelAndView("user/nominee");
        modelAndView.addObject("user", user);
        modelAndView.addObject("songs", songRepository.getByUserId(user.userId));
        return modelAndView;
    }

    @PostMapping("/api/recommend")
    @ResponseBody
    public String createSongAPI(HttpServletResponse response,
                                @RequestAttribute("user") User user,
                                @RequestParam("name") String name,
                                @RequestParam("artist") String artist,
                                @RequestParam("reason") String reason,
                                @RequestParam("pic") MultipartFile pic,
                                @RequestParam("intro") String intro) {
        var now = LocalDateTime.now();
        if (now.isAfter(votingDeadline)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "投票已结束";
        }

        if (name.length() > 50 || reason.length() > 100 || intro.length() > 50) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "输入的内容过长";
        }

        if (pic.getSize() > 5 * 1024 * 1024) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "图片大小超过5MB";
        }

        if (pic.getContentType() == null || !pic.getContentType().startsWith("image/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "不支持的图片格式";
        }

        String picName;
        try {
            picName = imageService.saveImage(pic);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "保存图片失败";
        }

        var song = new Song();
        song.name = name;
        song.artist = artist;
        song.reason = reason;
        song.pic = picName;
        song.intro = intro;
        song.state = NominationState.PENDING;
        song.votes = 0;
        song.userId = user.userId;

        songRepository.save(song);

        response.setStatus(HttpServletResponse.SC_CREATED);
        return "推荐成功";
    }
}
