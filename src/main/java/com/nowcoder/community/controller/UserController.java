package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
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
public class UserController {

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

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片！");
            return "/site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确！");
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
            logger.error("上传文件失败！" + ioException.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", ioException);
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
            logger.error("读取图像失败！" + ioException.getMessage());
        }
    }

    @LoginRequired
    @PostMapping("modified-password")
    public String modifiedPassword(String oldPassword, String newPassword, String confirmPassword, Model model) {
        User user = hostHoler.getUser();
        if (StringUtils.isBlank(oldPassword) || user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt())) == false) {
            model.addAttribute("oldMsg", "原始密码错误，请重新输入！");
            return "/site/setting";
        }
        if (StringUtils.isBlank(newPassword)) {
            model.addAttribute("newMsg", "新密码错误，请重新输入！");
            return "/site/setting";
        }
        if (StringUtils.isBlank(confirmPassword) || newPassword.equals(confirmPassword) == false) {
            model.addAttribute("confirmMsg", "确认密码与新密码不一致，请检查后输入！");
            return "/site/setting";
        }
        userService.updatePassword(user.getId(), newPassword);
        return "redirect:/index";
    }
}
