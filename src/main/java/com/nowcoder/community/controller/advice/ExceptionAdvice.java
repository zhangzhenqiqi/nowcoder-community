package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//@ControllerAdvice(annotations = {Controller.class})
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest req, HttpServletResponse res) throws IOException {
        logger.error("服务器发生异常：" + e + "\n" + e.getMessage());
        for (StackTraceElement ste : e.getStackTrace()) {
            logger.error(ste.toString());
        }
        String xRequestedWith = req.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {//异步请求
            res.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = res.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        } else {
            res.sendRedirect(req.getContextPath() + "/error");
        }
    }
}
