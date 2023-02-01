package com.pedro.auth.subject;

import com.pedro.auth.model.User;

/**
 * 函数式接口，要求通过用户名获得密码和权限
 */
@FunctionalInterface
public interface UserAccessFunction {

    /**
     * 用户实现方法
     */
    User userAccessFunction(String username);
}
