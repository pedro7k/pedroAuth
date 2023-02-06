package com.pedro.auth.util;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie工具类
 */
public class CookieUtil {

    /**
     * 从cookie中获取数据
     *
     * @param request
     * @param name
     * @return
     */
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("[pedroAuth] CookieUtil 参数为空！");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 向cookie中设置token
     *
     * @param response
     * @param value
     */
    public static void setTokenCookie(HttpServletResponse response, String value) {
        if (response == null || value == null) {
            throw new IllegalArgumentException("[pedroAuth] CookieUtil 参数为空！");
        }

        Cookie cookie = new Cookie("token", value);
        // cookie七天有效期
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);

    }

    /**
     * 清除指定cookie中的token
     *
     * @param response
     */
    public static void removeTokenCookie(HttpServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("[pedroAuth] CookieUtil 参数为空！");
        }

        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
