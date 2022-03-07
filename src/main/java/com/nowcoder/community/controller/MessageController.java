package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHoler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHoler hostHoler;

    /**
     * letter：消息
     * conversation：会话，两个用户之间的
     *
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHoler.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversations != null) {
            for (Message message : conversationList) {
                Map<String, Object> m = new HashMap<>();
                m.put("conversation", message);
                m.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                m.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                m.put("target", userService.findUserById(targetId));
                conversations.add(m);
            }
        }
        model.addAttribute("conversations", conversations);
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        return "/site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> m = new HashMap<>();
                m.put("letter", message);
                m.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(m);
            }
        }
        model.addAttribute("letters", letters);
        //私信目标
        model.addAttribute("target", getLetterTarget(conversationId));
        //设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }


    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int d0 = Integer.parseInt(ids[0]);
        int d1 = Integer.parseInt(ids[1]);
        if (hostHoler.getUser().getId() == d0) {
            return userService.findUserById(d1);
        } else {
            return userService.findUserById(d0);
        }
    }

    /**
     * 得到集合letterList中未读的消息id
     * @param letterList
     * @return
     */
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (ids != null) {
            for (Message message : letterList) {
                if (hostHoler.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在!");
        }
        Message message = new Message();
        message.setFromId(hostHoler.getUser().getId());
        message.setToId(target.getId());
        message.setConversationId(Math.min(message.getFromId(), message.getToId()) + "_" +
                Math.max(message.getFromId(), message.getToId()));
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }
}
