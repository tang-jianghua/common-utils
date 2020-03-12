/*
 * Copyright (c) 2019. 唐江华 保留所有权。
 */
package com.jh.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * @author tangjianghua
 * @date 2020/1/3
 * @time 16:44
 */
public class RegExpUtils {

    public static final String REGULAR_PHONE_CN = "^((\\+86)?1)[3456789]\\d{9}$";

    public static boolean matchePhoneCN(String phone){
        return Pattern.compile(REGULAR_PHONE_CN).matcher(phone).matches();
    }

    public static void main(String[] args) {
        System.out.println(LocalDate.now().toString());
    }


}
