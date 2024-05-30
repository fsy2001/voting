package com.tuanyi.voting.controller;

import com.tuanyi.voting.model.NominationType;
import com.tuanyi.voting.model.Nominee;
import com.tuanyi.voting.model.User;
import com.tuanyi.voting.repository.NomineeRepository;
import com.tuanyi.voting.model.NominationState;
import com.tuanyi.voting.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NomineeController {
    private final NomineeRepository nomineeRepository;
    private final ImageService imageService;

    public NomineeController(NomineeRepository nomineeRepository, ImageService imageService) {
        this.nomineeRepository = nomineeRepository;
        this.imageService = imageService;
    }

    @GetMapping("/vote")
    public ModelAndView vote(HttpServletRequest request) {
        var user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            return new ModelAndView("redirect:/uis");
        }

        var nominees = nomineeRepository.getNomineeByStateOrderByVotesDesc(NominationState.APPROVED);

        var modelAndView = new ModelAndView("vote");
        modelAndView.addObject("nominees", nominees);
        return modelAndView;
    }

    @GetMapping("/recommend")
    public String nominee(HttpServletRequest request) {
        var user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            return "redirect:/uis";
        }

        return "recommend";
    }

    @GetMapping("/nominee")
    public ModelAndView myNominee(HttpServletRequest request) {
        var user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            return new ModelAndView("redirect:/uis");
        }

        var modelAndView = new ModelAndView("nominee");
        modelAndView.addObject("user", user);
        modelAndView.addObject("nominees", nomineeRepository.getNomineeByUserId(user.getUserId()));
        return modelAndView;
    }

    @PostMapping("/api/recommend")
    @ResponseBody
    public String createNominee(HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestParam("type") String type,
                                @RequestParam("name") String name,
                                @RequestParam("reason") String reason,
                                @RequestParam("pic") MultipartFile pic,
                                @RequestParam("intro") String intro,
                                @RequestParam("contact") String contact) {
        var user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "尚未登录";
        }

        if (reason.length() > 100 || intro.length() > 50 || contact.length() > 50) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "输入的内容过长";
        }

        if (pic.getSize() > 5 * 1024 * 1024) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "图片大小超过5MB";
        }

        String picName;
        try {
            picName = imageService.saveImage(pic);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "保存图片失败";
        }

        Nominee nominee = new Nominee();
        nominee.setType(NominationType.valueOf(type));
        nominee.setName(name);
        nominee.setReason(reason);
        nominee.setPic(picName);
        nominee.setIntro(intro);
        nominee.setContact(contact);
        nominee.setState(NominationState.PENDING);
        nominee.setVotes(0);
        nominee.setUserId(user.getUserId());

        nomineeRepository.save(nominee);

        return "创建成功";
    }
}
