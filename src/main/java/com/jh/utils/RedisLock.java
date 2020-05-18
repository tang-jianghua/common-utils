/*
 * Copyright (c) 2019. 唐江华 保留所有权。
 */

package com.jh.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisOperations;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author tangjianghua
 * date 2020/4/8
 * time 9:58
 */
public class RedisLock implements Lock {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String key;

    private final String value = "LOCK";

    private RedisOperations redisOperations;

    /**
     * 尝试获取锁的间隔 1s
     */
    private final long INTERVAL = 1000L;

    /**
     * 获取锁的最大尝试次数
     */
    private int maxtry;

    /**
     * 默认获取锁的最大尝试次数
     */
    private static final int DEFUALT_MAXTRY=10;

    /**
     * 获取锁状态
     * true：获取到锁
     * false： 为获取到锁
     */
    private boolean lock = false;

    /**
     * 默认超时时间 10分钟
     */
    private final long DEFAILT_EXPIRE = 10L;

    /**
     * 默认超时单位
     */
    private final TimeUnit DEFAILT_UNIT = TimeUnit.MILLISECONDS;

    public RedisLock(RedisOperations redisOperations,String key) {
        this.key = key;
        this.redisOperations =redisOperations;
        maxtry=DEFUALT_MAXTRY;
    }

    /**
     * 设置最大尝试次数
     * @param maxtry
     */
    public void setMaxtry(int maxtry) {
        if(maxtry<1){
            maxtry=1;
        }
        if(maxtry > DEFUALT_MAXTRY){
            maxtry = DEFUALT_MAXTRY;
        }
        this.maxtry = maxtry;
    }

    @Override
    public void lock() {
        this.lock = redisOperations.opsForValue().setIfAbsent(key, value);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
      //TODO
    }

    @Override
    public boolean tryLock() {
        int i = 0;
        do{
            logger.info("线程"+Thread.currentThread().getId()+"--第"+i+"次尝试获取锁--"+key);
            lock();
            if(!lock){
                try {
                    Thread.sleep(INTERVAL);
                    i++;
                    continue;
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }else {
                logger.info("线程"+Thread.currentThread().getId()+"--获取到锁--"+key);
                break;
            }
        }while (i < maxtry);
        return lock;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit){
        if(time == 0L){
            time = DEFAILT_EXPIRE;
        }
        if(unit == null){
            unit = DEFAILT_UNIT;
        }
        if(!tryLock()){
            return false;
        }
        Boolean aBoolean = false;
        try {
            logger.info("线程"+Thread.currentThread().getId()+"--为锁添加超时--"+key+"--"+time+"--"+unit.name());
            aBoolean = redisOperations.expire(key, time, unit);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        if(!aBoolean){
            unlock();
        }
        return this.lock;
    }


    /**
     * 默认超时获取锁
     * @return
     */
    public boolean tryLockDefaultExpire(){
        return  tryLock(0L, null);
    }

    @Override
    public void unlock() {
        if(!lock){
            logger.info("线程"+Thread.currentThread().getId()+"--未获取到锁，无需释放--"+key);
          return;
        }
        redisOperations.delete(key);
        this.lock = false;
        logger.info("线程"+Thread.currentThread().getId()+"--释放锁--"+key);
    }

    /**
     * 是否拥有锁
     * @return
     */
    public boolean isLock(){
        return lock;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}

