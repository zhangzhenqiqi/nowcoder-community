package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.WebsiteStatsService;
import com.nowcoder.community.util.HostHoler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class WebsiteStatsInterceptor implements HandlerInterceptor {
    @Autowired
    private WebsiteStatsService websiteStatsService;
    @Autowired
    private HostHoler hostHoler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计uv
        String ip = request.getRemoteHost();
        websiteStatsService.recordUV(ip);
        //统计DAU
        User user = hostHoler.getUser();
        if (user != null) {
            websiteStatsService.recordDAU(user.getId());
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
