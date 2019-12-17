package com.jh.utils;

/**
 * @author tangjianghua
 * @data @data
 */
public class ShortUtils {

    /**
     *@param: s
     *@description: short转字节数组,正常情况下不会使用，因为short转byte为上转下，会丧失高位数据,此方法获取short的两个字节，高位在前地位在后。
     *@author: tangjianghua
     */
    public static byte[] toBytes(Short s){
      return new byte[]{(byte)(s>>8),s.byteValue()};
    }
}
