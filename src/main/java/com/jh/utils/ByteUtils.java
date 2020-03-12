package com.jh.utils;

/**
 * @author tangjianghua
 * @data @data
 */
public class ByteUtils {


    /**
     *@param: b8 低8位
     *@param: b16 低9-16位
     *@param: b24 低17-24位
     *@param: b32 低25-32位
     *@description: 将不同位的字节组合为int
     *@author: tangjianghua
     */
    public static int toInt(byte b8,byte b16,byte b24,byte b32){
        return b8 | b16<<8 | b24<<16 | b32<<24;
    }


}
