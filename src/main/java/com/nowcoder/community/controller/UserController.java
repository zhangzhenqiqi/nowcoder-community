package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHoler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHoler hostHoler;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "???????????????????????????");
            return "/site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "????????????????????????");
            return "/site/setting";
        }

        filename = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + filename);
        try {
            if (!dest.exists()) {
                dest.mkdirs();
            }
            headerImage.transferTo(dest);
        } catch (IOException ioException) {
            logger.error("?????????????????????" + ioException.getMessage());
            throw new RuntimeException("?????????????????????????????????????????????", ioException);
        }

        User user = hostHoler.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @GetMapping("/header/{filename}")
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        filename = uploadPath + "/" + filename;
        response.setContentType("image/" + suffix);
        try (FileInputStream fis = new FileInputStream(filename)) {
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException ioException) {
            logger.error("?????????????????????" + ioException.getMessage());
        }
    }

    @LoginRequired
    @PostMapping("modified-password")
    public String modifiedPassword(String oldPassword, String newPassword, String confirmPassword, Model model) {
        User user = hostHoler.getUser();
        if (StringUtils.isBlank(oldPassword) || user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt())) == false) {
            model.addAttribute("oldMsg", "???????????????????????????????????????");
            return "/site/setting";
        }
        if (StringUtils.isBlank(newPassword)) {
            model.addAttribute("newMsg", "????????????????????????????????????");
            return "/site/setting";
        }
        if (StringUtils.isBlank(confirmPassword) || newPassword.equals(confirmPassword) == false) {
            model.addAttribute("confirmMsg", "?????????????????????????????????????????????????????????");
            return "/site/setting";
        }
        userService.updatePassword(user.getId(), newPassword);
        return "redirect:/index";
    }

    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("??????????????????!");
        }
        model.addAttribute("user", user);
        model.addAttribute("likeCount", likeService.findUserLikeCount(user.getId()));
        //?????????????????????????????????????????????
        User loginUser = hostHoler.getUser();
        if (loginUser != null)
            model.addAttribute("followed",
                    followService.getFollowEntityStatus(loginUser.getId(), ENTITY_TYPE_USER, userId));
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, ENTITY_TYPE_USER));
        model.addAttribute("followerCount", followService.getFollowerCount(ENTITY_TYPE_USER, userId));
        return "/site/profile";
    }


}
