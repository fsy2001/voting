package com.tuanyi.voting.controller;

import com.tuanyi.voting.model.NominationState;
import com.tuanyi.voting.model.NominationType;
import com.tuanyi.voting.model.Nominee;
import com.tuanyi.voting.model.User;
import com.tuanyi.voting.repository.NomineeRepository;
import com.tuanyi.voting.service.ImageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/recommend")
    public String recommendNomineePage() {
        return "user/recommend";
    }

    @GetMapping("/nominee")
    public ModelAndView myNomineePage(@RequestAttribute("user") User user) {
        var modelAndView = new ModelAndView("user/nominee");
        modelAndView.addObject("user", user);
        modelAndView.addObject("nominees", nomineeRepository.getNomineeByUserId(user.userId));
        return modelAndView;
    }

    @PostMapping("/api/recommend")
    @ResponseBody
    public String createNomineeAPI(HttpServletResponse response,
                                   @RequestAttribute("user") User user,
                                   @RequestParam("type") String type,
                                   @RequestParam("name") String name,
                                   @RequestParam("reason") String reason,
                                   @RequestParam("pic") MultipartFile pic,
                                   @RequestParam("intro") String intro,
                                   @RequestParam("contact") String contact) {

        if (name.length() > 50 || reason.length() > 100 || intro.length() > 50 || contact.length() > 50) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "输入的内容过长";
        }

        NominationType nominationType;
        try {
            nominationType = NominationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "不合法的参数";
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

        var nominee = new Nominee();
        nominee.type = nominationType;
        nominee.name = name;
        nominee.reason = reason;
        nominee.pic = picName;
        nominee.intro = intro;
        nominee.contact = contact;
        nominee.state = NominationState.PENDING;
        nominee.votes = 0;
        nominee.userId = user.userId;

        nomineeRepository.save(nominee);

        return "创建成功";
    }
}
