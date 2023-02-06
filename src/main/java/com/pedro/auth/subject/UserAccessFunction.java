package com.pedro.auth.subject;

import com.pedro.auth.model.User;

/**
 * 函数式接口，要求通过用户名获得数据库中存储的原文密码和权限等信息
 */
@FunctionalInterface
public interface UserAccessFunction {

    /**
     * 用户实现方法
     */
    User getUserInfo(String username);
}
