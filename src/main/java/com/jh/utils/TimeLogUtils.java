/*
 * Copyright (c) 2019. 唐江华 保留所有权。
 */

package com.jh.utils;

import java.util.*;

/**
 * @author tangjianghua
 * @data @data
 */
public class TimeLogUtils {

    private static final String logFormatMillis = "距离上次标记耗时---%s---%s毫秒";
    private static final String logFormatMillisTotal = "总耗时---%s---%s毫秒";
    private static final String logFormatSeconds = "距离上次标记耗时---%s---%s秒";
    private static final String logFormatSecondsTotal = "总耗时---%s---%s秒";

    private final static ThreadLocal<Map<String, TimeNode>> THREAD_LOCAL = new ThreadLocal<>();

    private static Map<String, TimeNode> get() {
        Map<String, TimeNode> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new HashMap<>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    /**
     * 标记一个任务，不同任务之间互不影响。
     *
     * @param mark
     */
    public static void mark(String mark) {
        if (mark == null) {
            throw new NullPointerException("mark can not be null.");
        }
        mark1(mark);
    }

    /**
     * 默认标记
     */
    public static void mark() {
        mark1(null);
    }

    /**
     * 清理所有的时间记录副本
     */
    public static void clearAll() {
        THREAD_LOCAL.set(null);
    }

    /**
     * 清理mark的时间记录副本
     *
     * @param mark
     */
    public static void clear(String mark) {
        if (mark == null) {
            throw new NullPointerException("mark can not be null.");
        }
        clear1(mark);
    }

    /**
     * 清理默认的时间记录副本
     */
    public static void clear() {
        clear1(null);
    }


    /**
     * 清理mark的时间记录副本
     *
     * @param mark
     */
    private static void clear1(String mark) {
        Map<String, TimeNode> map = THREAD_LOCAL.get();
        if (map == null) {
            return;
        }
        map.remove(mark);
    }

    private static void mark1(String mark) {
        Map<String, TimeNode> map = get();
        TimeNode timeNode = map.get(mark);
        if (timeNode == null) {
            map.put(mark, new TimeNode(System.currentTimeMillis()));
        } else {
            map.put(mark, new TimeNode(timeNode, System.currentTimeMillis()));
        }
    }

    /**
     * 计算距离上次mark标记消耗的时间 单位：毫秒
     *
     * @param mark
     * @return
     */
    private static long lastConsumingMillis1(String mark) {
        Map<String, TimeNode> map = get();
        TimeNode timeNode = map.get(mark);
        if (timeNode == null) {
            throw new NoSuchElementException("no mark " + mark + " was logged.");
        }
        return System.currentTimeMillis() - timeNode.millis;
    }

    /**
     * 总耗时 单位：毫秒
     *
     * @param mark
     * @return
     */
    private static long totalConsumingMillis1(String mark) {
        TimeNode timeNode = get().get(mark);
        if (timeNode == null) {
            throw new NoSuchElementException("no mark " + mark + " was logged.");
        }
        return System.currentTimeMillis() - timeNode.first.millis;
    }

    /**
     * 计算距离上次mark标记消耗的时间,同时标记mark 单位：毫秒
     *
     * @param mark
     * @return
     */
    public static String lastConsumingMillis(String mark) {
        if (mark == null) {
            throw new NullPointerException("mark can not be null.");
        }
        return String.format(logFormatMillis, mark, lastConsumingMillis1(mark));
    }

    /**
     * 计算距离上次mark标记消耗的时间,同时标记mark 单位：秒
     *
     * @param mark
     * @return
     */
    public static String lastConsumingSeconds(String mark) {
        if (mark == null) {
            throw new NullPointerException("mark can not be null.");
        }
        return String.format(logFormatSeconds, mark, lastConsumingMillis1(mark) / 1000);
    }

    /**
     * mark总耗时 单位：秒
     *
     * @param mark
     * @return
     */
    public static String totalConsumingMillis(String mark) {
        if (mark == null) {
            throw new NullPointerException("mark can not be null.");
        }
        return String.format(logFormatMillisTotal, mark, lastConsumingMillis1(mark));
    }

    /**
     * mark总耗时 单位：秒
     *
     * @param mark
     * @return
     */
    public static String totalConsumingSeconds(String mark) {
        if (mark == null) {
            throw new NullPointerException("mark can not be null.");
        }
        return String.format(logFormatSecondsTotal, mark, lastConsumingMillis1(mark) / 1000);
    }

    /**
     * 计算距离上次mark标记消耗的时间,同时标记 单位：毫秒
     *
     * @return
     */
    public static String lastConsumingMillis() {
        return String.format(logFormatMillis, "", lastConsumingMillis1(null));
    }

    /**
     * 计算距离上次mark标记消耗的时间,同时标记 单位：秒
     *
     * @return
     */
    public static String lastConsumingSeconds() {
        return String.format(logFormatSeconds, "", lastConsumingMillis1(null) / 1000);
    }

    /**
     * 默认总耗时 单位：豪秒
     *
     * @return
     */
    public static String totalConsumingMillis() {
        return String.format(logFormatMillisTotal, "", totalConsumingMillis1(null));
    }

    /**
     * 默认总耗时 单位：秒
     *
     * @return
     */
    public static String totalConsumingSeconds() {
        return String.format(logFormatSecondsTotal, "", totalConsumingMillis1(null) / 1000);
    }

    /**
     * 统计默认的所有阶段的耗时
     *
     * @return
     */
    public static String smartConsumingMillis() {
        StringBuilder stringBuilder = new StringBuilder();
        TimeNode timeNode = get().get(null);
        if (timeNode == null) {
            return "未记录";
        }
        stringBuilder.append("耗时统计:\n");
        List<Long> longs = smartConsumingMillils1(timeNode);
        for (int i = longs.size() - 1; i >= 0; i--) {
            stringBuilder.append("第 ")
                    .append(longs.size() - i)
                    .append(" 次耗时------")
                    .append(longs.get(i))
                    .append("豪秒\n");
        }
        return stringBuilder.append(totalConsumingMillis()).toString();
    }

    /**
     * 统计默认的所有阶段的耗时
     *
     * @return
     */
    public static String smartConsumingSeconds() {
        StringBuilder stringBuilder = new StringBuilder();
        TimeNode timeNode = get().get(null);
        if (timeNode == null) {
            return "未记录";
        }
        stringBuilder.append("耗时统计:\n");
        List<Long> longs = smartConsumingMillils1(timeNode);
        for (int i = longs.size() - 1; i >= 0; i--) {
            stringBuilder.append("第 ")
                    .append(longs.size() - i)
                    .append(" 次耗时------")
                    .append(longs.get(i) / 1000)
                    .append("秒\n");
        }
        return stringBuilder.append(totalConsumingSeconds()).toString();
    }

    /**
     * 统计默认的所有阶段的耗时
     *
     * @return
     */
    public static String smartConsumingMillis(String mark) {
        StringBuilder stringBuilder = new StringBuilder();
        TimeNode timeNode = get().get(mark);
        if (timeNode == null) {
            return "未记录";
        }
        stringBuilder.append(mark)
                .append("耗时统计:\n");
        List<Long> longs = smartConsumingMillils1(timeNode);
        for (int i = longs.size() - 1; i >= 0; i--) {
            stringBuilder.append("第 ")
                    .append(longs.size() - i)
                    .append(" 次耗时------")
                    .append(longs.get(i))
                    .append("豪秒\n");
        }
        return stringBuilder.append(totalConsumingMillis(mark)).toString();
    }

    /**
     * 统计默认的所有阶段的耗时
     *
     * @return
     */
    public static String smartConsumingSeconds(String mark) {
        StringBuilder stringBuilder = new StringBuilder();
        TimeNode timeNode = get().get(mark);
        if (timeNode == null) {
            return "未记录";
        }
        stringBuilder.append(mark)
                .append("耗时统计:\n");
        List<Long> longs = smartConsumingMillils1(timeNode);
        for (int i = longs.size() - 1; i >= 0; i--) {
            stringBuilder.append("第 ")
                    .append(longs.size() - i)
                    .append(" 次耗时------")
                    .append(longs.get(i) / 1000)
                    .append("秒\n");
        }
        return stringBuilder.append(totalConsumingSeconds(mark)).toString();
    }

    /**
     * 将每次的耗时统计到集合里
     *
     * @param timeNode
     * @return
     */
    private static List<Long> smartConsumingMillils1(TimeNode timeNode) {
        List<Long> times = new ArrayList<>();
        times.add(System.currentTimeMillis() - timeNode.millis);
        TimeNode temp;
        while ((temp = timeNode.last) != null) {
            times.add(timeNode.millis - temp.millis);
            timeNode = temp;
        }
        return times;
    }


    private static final class TimeNode {

        /**
         * 上个节点
         */
        private final TimeNode last;

        /**
         * 当前时间，毫秒
         */
        private final long millis;

        /**
         * 队列首节点
         */
        private final TimeNode first;


        public TimeNode(TimeNode last, long millis) {
            this.last = last;
            this.millis = millis;
            if (last == null) {
                first = this;
            } else {
                first = last.first;
            }
        }

        public TimeNode(long millis) {
            this(null, millis);
        }


    }

}
