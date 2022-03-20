package com.nowcoder.community.controller;

import com.nowcoder.community.service.WebsiteStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class WebsiteStatsController {
    @Autowired
    private WebsiteStatsService websiteStatsService;

    @RequestMapping(value = "/stats", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage() {
        return "/site/admin/data";
    }

    @PostMapping("/stats/uv")
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long uv = websiteStatsService.calculateUV(start, end);
        model.addAttribute("uvStats", uv);
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate", end);
        return "forward:/stats";
    }

    @PostMapping("/stats/dau")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long dau = websiteStatsService.calculateDAU(start, end);
        model.addAttribute("dauStats", dau);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);
        return "forward:/stats";
    }
}
