package com.pedro.auth.context;

import com.pedro.auth.subject.UserAccessFunction;

/**
 * 用户数据获取上下文，维护了当前系统用于获取用户信息的方法
 */
public class UserAccessFunctionContext {

    private static UserAccessFunction userAccessFunction;

    public UserAccessFunctionContext() {
    }

    public static UserAccessFunction getUserAccessFunction() {
        return userAccessFunction;
    }

    public static void setUserAccessFunction(UserAccessFunction userAccessFunction) {
        UserAccessFunctionContext.userAccessFunction = userAccessFunction;
    }
}
