package com.pedro.auth.config;

import com.pedro.auth.model.User;
import com.pedro.auth.subject.AuthSubject;
import org.springframework.stereotype.Component;

/**
 * 用户信息上下文
 */
@Component
public class UserContextHolder {

    private final ThreadLocal<AuthSubject> userContext = new ThreadLocal<AuthSubject>();

    public void setUserContext(AuthSubject authSubject){
        userContext.set(authSubject);
    }

    public AuthSubject getUserContext(){
        return userContext.get();
    }

    public void clearUserContext(){
        userContext.remove();
    }
}
