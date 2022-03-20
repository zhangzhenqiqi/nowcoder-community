package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class QuartzTest {
    @Autowired
    private Scheduler scheduler;

    @Test
    public void testScheduler() {
        try {
            boolean ok = scheduler.deleteJob(new JobKey("alphaJob", "alphaJobGroup"));
            System.out.println(ok);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
