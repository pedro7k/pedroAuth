package com.pedro.auth.util;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie工具类
 */
public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("[pedroAuth] CookieUtil 参数为空！");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void setTokenCookie(HttpServletResponse response, String value) {
        if (response == null || value == null) {
            throw new IllegalArgumentException("[pedroAuth] CookieUtil 参数为空！");
        }

        Cookie cookie = new Cookie("token", value);
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);

    }
}
