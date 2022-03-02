package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 用于代替Session对象，持有用户信息,为什么不用session存而用ThreadLocal》
 *  在分布式的环境下，使用session存在共享数据的问题。通常的解决方案，是将共享数据存入数据库，
 *  所有的应用服务器都去数据库获取共享数据。对于每一次请求，开始时从数据库里取到数据，然后将其临时存放在本地的内存里，
 *  考虑到线程之间的隔离，所以用threadlocal，这样在本次请求的过程中，就可以随时获取到这份共享数据了。
 *  所以，session的替代方案是数据库，ThreadLocal只是打了个辅助。以上的内容，我在课上也讲啦，你不要追求速度，要注重听课的质量呀。
 * */
@Component
public class HostHoler {
    private ThreadLocal<User> users = new ThreadLocal<User>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
