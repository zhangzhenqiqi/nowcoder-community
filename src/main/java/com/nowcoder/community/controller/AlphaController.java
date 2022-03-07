package com.nowcoder.community.controller;

import com.nowcoder.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot!";
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String val = request.getHeader(name);
            System.out.println(name + ":" + val);
        }
        System.out.println(request.getParameter("code"));

        response.setContentType("text/html;charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write("<h1>nowcoder</h1>");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(@RequestParam(name = "current", required = false, defaultValue = "1") int current,
                              @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current + " " + limit);
        return "some students";
    }

    @GetMapping(path = "/student/{id}")
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        return "one student" + id;
    }

    @PostMapping(path = "/student")
    @ResponseBody
    public String saveStudent(@RequestParam("name") String name,
                              @RequestParam("age") int age) {
        System.out.println(name + " " + age);
        return "success";
    }

    @GetMapping(path = "/teacher")
//    @ResponseBody
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "张振琦");
        mav.addObject("age", "24");
        mav.setViewName("/demo/view");
        return mav;//???
    }

    @GetMapping("/school")
    public String getSchool(Model model) {
        model.addAttribute("name", "北大");
        model.addAttribute("age", "88");
        return "/demo/view";
    }

    @GetMapping("/emp")
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> m = new HashMap<>();
        m.put("name", "张振琦");
        m.put("age", 23);
        m.put("gender", "male");
        m.put("salary", 8000.00);
        return m;
    }

    @GetMapping("/emps")
    @ResponseBody
    public Map<String, Object>[] getEmps() {
        Map<String, Object>[] m = new HashMap[2];
        m[0] = new HashMap<>();
        m[1] = new HashMap<>();
        m[0].put("name", "张振琦");
        m[0].put("age", 23);
        m[0].put("gender", "male");
        m[0].put("salary", 8000.00);
        m[1].put("name", "ss");
        m[1].put("age", 233);
        m[1].put("gender", "female");
        m[1].put("salary", 80800.00);
        return m;
    }

    @GetMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        cookie.setPath("/community/alpha");
        cookie.setMaxAge(60 * 10);
        response.addCookie(cookie);
        return "set cookie";
    }

    @GetMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "zzq");
        return "set session";
    }

    @PostMapping("/ajax")
    @ResponseBody//如果不加此注解，则会将JSONString视作视图，找不到这个视图会报错！！
    public String ajax(String name, int age, HttpServletResponse response) {
        System.out.println(name + " " + age);
        return CommunityUtil.getJSONString(100, "success");
    }
}
