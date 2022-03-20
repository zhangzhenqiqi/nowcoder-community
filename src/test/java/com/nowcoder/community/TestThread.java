package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class TestThread {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private AlphaService alphaService;

    private void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                sleep(1000);
                logger.info("hello");
            }
        };
        for (int i = 0; i < 10; i++) {
            taskExecutor.execute(task);
        }
        sleep(10000);

    }

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
//                sleep(1000);
                logger.info("hello");
            }
        };
        taskScheduler.scheduleAtFixedRate(task, 1000);
        sleep(10000);
    }

    @Test
    public void testSimple() {
//        alphaService.execute2();
        sleep(1000 * 10);
    }
}
