package com.nowcoder.community.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype")
public class AlphaService {

    public AlphaService() {
        System.out.println("实例化 AlphaService");
    }

    @PostConstruct
    public void init() {
        System.out.println("init alpha-service");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("destroy AlphaService");
    }
}
