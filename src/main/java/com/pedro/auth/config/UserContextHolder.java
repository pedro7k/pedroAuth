package com.pedro.auth.config;

import com.pedro.auth.model.User;
import com.pedro.auth.subject.AuthSubject;
import com.pedro.auth.subject.impl.DefaultAuthSubject;
import org.springframework.stereotype.Component;

/**
 * 用户信息上下文
 */
public class UserContextHolder {

    private static final ThreadLocal<AuthSubject> userContext = ThreadLocal.withInitial(DefaultAuthSubject::new);

    public static void setUserContext(AuthSubject authSubject) {
        userContext.set(authSubject);
    }

    public static AuthSubject getUserContext() {
        return userContext.get();
    }

    public static void clearUserContext() {
        userContext.remove();
    }
}
