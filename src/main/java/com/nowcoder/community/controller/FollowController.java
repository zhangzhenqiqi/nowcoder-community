package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHoler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService followService;

    @Autowired
    private HostHoler hostHoler;

    @Autowired
    private UserService userService;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHoler.getUser();
        followService.follow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已关注");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHoler.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已取消关注");
    }

    @GetMapping("/followees/{userId}")
    public String getFolloweePage(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        User loginUser = hostHoler.getUser();
        model.addAttribute("user", user);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.getFolloweeCount(user.getId(), ENTITY_TYPE_USER));
        List<Map<String, Object>> list = followService.getFollowees(
                user.getId(), ENTITY_TYPE_USER, page.getOffset(), page.getLimit());
        if (loginUser != null) {
            for (Map<String, Object> map : list) {
                map.put("followed", CODE_FOLLOW);
            }
        }
        model.addAttribute("list", list);
        return "/site/followee";
    }

    /**
     * userId对应的用户（不一定为loginUser）的关注界面
     *
     * @param userId
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/followers/{userId}")
    public String getFollowerPage(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        User loginUser = hostHoler.getUser();
        model.addAttribute("user", user);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.getFollowerCount(ENTITY_TYPE_USER, user.getId()));
        List<Map<String, Object>> list = followService.getFollowers(
                user.getId(), ENTITY_TYPE_USER, page.getOffset(), page.getLimit());
        if (loginUser != null) {
            for (Map<String, Object> map : list) {
                map.put("followed", followService.getFollowEntityStatus(
                        loginUser.getId(), ENTITY_TYPE_USER, ((User) map.get("user")).getId()));
            }
        }
        model.addAttribute("list", list);
        return "/site/follower";
    }
}
