package com.video.utils;

import org.springframework.stereotype.Component;

/**
 * @ClassName: RegExpUtil
 * @author: 赵容庆
 * @date: 2022年09月23日 9:43
 * @Description: TODO
 */

@Component
public class RegExpUtil {
    private static final String EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*\n";
    private static final String WANGYI_EMAIL = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
    private static final String QQ_EMAIL = "[1-9]\\d{7,10}@qq\\.com";
    private static final String USER_NAME = "[1-9]([0-9]{8,11})";
    private static final String TELEPHONE = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
    /**
     * 至少一个字母和一个数字
     */
    private static final String PASSWORD = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";;

    public static boolean matchEmail(String email) {
        return email.matches(EMAIL) || email.matches(QQ_EMAIL) || email.matches(WANGYI_EMAIL);
    }

    public static boolean matchUsername(String username) {
        return username.matches(USER_NAME);
    }

    public static boolean matchPassword(String password) {
        return password.matches(PASSWORD);
    }

    public static boolean matchesTelephone(String telephone){
        return telephone.matches(TELEPHONE);
    }


}
