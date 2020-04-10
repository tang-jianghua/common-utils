/*
 * Copyright (c) 2020. 网联客（北京）科技有限公司 All rights reserved.
 */

package com.jh.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author tangjianghua
 * @date 2020/3/5
 * @time 17:48
 */
public class LockUtils {

    private static Map<String,ReentrantReadWriteLock> lockMap = new HashMap<>();

    /**
     * D0重新打款 %S---D0批次号
     */
    public static final String D0_REWIRE_KEY="D0_REWIRE_KEY_%s";
    /**
     * D0重新打款 %S---T1-D0批次号
     */
    public static final String T1_TO_D0_REWIRE_KEY="T1_TO_D0_REWIRE_KEY_%s";

    public synchronized static ReentrantReadWriteLock getLock(String key){
        ReentrantReadWriteLock reentrantReadWriteLock = lockMap.get(key);
        if(reentrantReadWriteLock==null){
             reentrantReadWriteLock =  new ReentrantReadWriteLock();
            lockMap.put(key, reentrantReadWriteLock);
        }
        return reentrantReadWriteLock;
    }
    public synchronized static void removeLock(String key){
        lockMap.remove(key);
    }
}
