package com.pedro.auth.subject.api;

import com.pedro.auth.config.UserContextHolder;
import com.pedro.auth.subject.AuthSubject;

/**
 * 对外工具类
 */
public class PedroAuthUtil {

    /**
     * 获得当前认证主体
     */
    public static AuthSubject getAuthSubject(){
        return UserContextHolder.getUserContext();
    }
}
