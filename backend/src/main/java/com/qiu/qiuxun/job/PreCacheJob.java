package com.qiu.qiuxun.job;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiu.qiuxun.model.po.User;
import com.qiu.qiuxun.model.vo.UserVO;
import com.qiu.qiuxun.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.qiu.qiuxun.constant.RedisConstant.*;

/**
 * @description: Redis缓存预热
 * @className: PreCacheJob.java
 * @author: qiu
 * @createTime: 2024/2/27 14:37
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private IUserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    /**
     * @description: 每日预热推荐用户的定时任务，只考虑预热重点用户的推荐
     * @params: []
     * @return: void
     * @author: qiu
     * @dateTime: 2024/2/27 14:59
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock(PRECACHE_JOB_LOCK);
        try {
            // 1.只有一个线程获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());
                List<Long> mainUsers = userService.getJobUsers();
                for (Long user : mainUsers) {
                    writeCache(user);
                }
                // 未登录用户的缓存
                writeCache(DEFAULT_RECOMMEND_USERID);
            }
        } catch (InterruptedException e) {
            log.error("do cache recommendUser error", e);
        } finally {
            // 2.只能释放自己的锁
            if (lock != null && lock.isHeldByCurrentThread()) {
                System.out.println("unLock:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    /**
     * @description: 写入缓存
     * @params: [user]
     * @return: void
     * @author: qiu
     * @dateTime: 2024/4/15 13:20
     */
    private void writeCache(Long user) {
        Page<User> page = Page.of(1, 20);
        Page<User> userPage = userService.page(page);
        List<User> records = userPage.getRecords();
        List<UserVO> userVOS = BeanUtil.copyToList(records, UserVO.class);
        String redisKey = USER_RECOMMEND_KEY + user;
        // 写缓存
        try {
            stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(userVOS), 48, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
    }
}
