package com.jh.utils;

import java.util.Arrays;
import java.util.Random;

/**
 * @author tangjianghua
 * @data @data
 */
public class ByteUtils {


    /**
     * @param: b8 低8位
     * @param: b16 低9-16位
     * @param: b24 低17-24位
     * @param: b32 低25-32位
     * @description: 将不同位的字节组合为int
     * @author: tangjianghua
     */
    public static int toInt(byte b8, byte b16, byte b24, byte b32) {
        return b8 | b16 << 8 | b24 << 16 | b32 << 24;
    }


    /**
     * 小端序 字节数组转int
     *
     * @param bytes
     * @return
     */
    public static int littleEndian(byte[] bytes) {
        return (bytes[0]&0XFF)
                | ((bytes[1]&0XFF) << 8)
                | ((bytes[2]&0XFF) << 16)
                | ((bytes[3]&0XFF) << 24);
    }

    /**
     * 大端序 字节数组转int
     *
     * @param bytes
     * @return
     */
    public static int bigEndian(byte[] bytes) {
        //System.out.println(String.format("%32s", Integer.toBinaryString(bytes[3]&0XFF)).replaceAll("\\s", "0"));
        //System.out.println(String.format("%32s", Integer.toBinaryString(((bytes[2]&0XFF) << 8)).replaceAll("\\s", "0")));
        //System.out.println(String.format("%32s", Integer.toBinaryString(((bytes[1]&0XFF) << 16)).replaceAll("\\s", "0")));
        //System.out.println(String.format("%32s", Integer.toBinaryString(((bytes[0]&0XFF) << 24)).replaceAll("\\s", "0")));

        return (bytes[3]&0XFF)
                | ((bytes[2]&0XFF) << 8)
                | ((bytes[1]&0XFF) << 16)
                | ((bytes[0]&0XFF) << 24);
    }

    /**
     * 大端序 int转字节数组
     *
     * @param i
     * @return
     */
    public static byte[] bigEndian(int i) {
        int byte1 = i & 0XFF;
        int byte2 = (i & 0XFFFF) >>> 8;
        int byte3 = (i & 0XFFFFFF) >>> 16;
        int byte4 = (i & 0XFFFFFFFF) >>> 24;
        return new byte[]{(byte) byte4, (byte) byte3, (byte) byte2, (byte) byte1};
    }

    /**
     * 小端序 int转字节数组
     *
     * @param i
     * @return
     */
    public static byte[] littleEndian(int i) {
        int byte1 = i & 0XFF;
        int byte2 = (i & 0XFF << 8) >> 8;
        int byte3 = (i & 0XFF << 16) >> 16;
        int byte4 = (i & 0XFF << 24) >> 24;
        return new byte[]{(byte) byte1, (byte) byte2, (byte) byte3, (byte) byte4};
    }


    public static void main(String[] args) {
        int a = new Random().nextInt();
        String s = String.format("%32s", Integer.toBinaryString(a)).replaceAll("\\s", "0");
        System.out.println("原数据:             " + s.substring(0, 8) + " " + s.substring(8, 16) + " " + s.substring(16, 24) + " " + s.substring(24, 32) + " ");

        byte[] bytes = bigEndian(a);
        System.out.printf("大端序-int转字节数组：");
        for (int i = 0; i < bytes.length; i++) {
            System.out.print(String.format("%8s", Integer.toBinaryString(bytes[i] & 0XFF)).replaceAll("\\s", "0") + " ");
        }
        System.out.println();
        System.out.println("大端序-字节数组转int验证:" + (bigEndian(bytes) == a));

        byte[] bytes2 = littleEndian(a);
        System.out.printf("小端序-int转字节数组：");
        for (int i = 0; i < bytes2.length; i++) {
            System.out.print(String.format("%8s", Integer.toBinaryString(bytes2[i] & 0XFF)).replaceAll("\\s", "0") + " ");
        }
        System.out.println();
        System.out.println("小端序-字节数组转int验证:" + (littleEndian(bytes2) == a));
    }
}
