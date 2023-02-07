package com.pedro.auth.subject.impl;

import com.pedro.auth.common.enums.EncryptionEnum;
import com.pedro.auth.common.enums.PedroAuthExceptionEnum;
import com.pedro.auth.common.exceptions.PedroAuthException;
import com.pedro.auth.config.UserAuthInterceptor;
import com.pedro.auth.context.UserAccessFunctionContext;
import com.pedro.auth.encryption.EncryptionContext;
import com.pedro.auth.encryption.EncryptionFacade;
import com.pedro.auth.model.User;
import com.pedro.auth.subject.AuthSubject;
import com.pedro.auth.subject.UserAccessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认权限主体实现
 */
public class DefaultAuthSubject implements AuthSubject {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthInterceptor.class);

    // TODO 当前所有用到创建AuthSubject的时候，都是直接用Default的，看一下怎么改。要改的话关注本类构造器的使用位置

    /**
     * 用户信息
     */
    private User user;

    /**
     * 是否记住我
     */
    private boolean rememberMe = false;

    /**
     * 是否在本次请求中待注销
     */
    private boolean logout = false;

    @Override
    public boolean login(String username, String password, UserAccessFunction userAccessFunction) {
        return login(username, password, null, userAccessFunction);
    }

    @Override
    public boolean login(String username, String password, EncryptionEnum encryptionType, UserAccessFunction userAccessFunction) {
        return login(username, password, encryptionType, false, userAccessFunction);
    }

    @Override
    public boolean login(String username, String password, boolean rememberMe, UserAccessFunction userAccessFunction) {
        return login(username, password, null, rememberMe, userAccessFunction);
    }

    @Override
    public boolean login(String username, String password, EncryptionEnum encryptionType, boolean rememberMe, UserAccessFunction userAccessFunction) {

        try {
            // 1.缓存当前的userAccessFunction
            UserAccessFunctionContext.setUserAccessFunction(userAccessFunction);

            // 2.获取数据库中存储的用户数据
            User user = userAccessFunction.getUserInfo(username);
            if (user == null) {
                throw new PedroAuthException(PedroAuthExceptionEnum.USERNAME_ERROR);
            }

            // 3.密码验证
            if (!user.getUsername().equals(username)) {
                throw new PedroAuthException(PedroAuthExceptionEnum.USERNAME_ERROR);
            }
            // 是否加密
            if (encryptionType != null) {
                // 3.1 获得密码工具
                EncryptionFacade encryptionTool = EncryptionContext.encryptionToolMap.get(encryptionType);
                // 3.2 加密传入的password
                String encodePassword = encryptionTool.encode(username, password, user.getSalt());
                // 3.3 校验加密后的传入密码
                if (!encodePassword.equals(user.getPassword())) {
                    throw new PedroAuthException(PedroAuthExceptionEnum.PASSWORD_ERROR);
                }
            } else if (!password.equals(user.getPassword())) {
                throw new PedroAuthException(PedroAuthExceptionEnum.PASSWORD_ERROR);
            }

            // 4.设置user
            setUser(user);

            // 5.设置记住我
            setRememberMe(rememberMe);

            // 6.返回
            return true;
        } catch (Throwable e) {
            logger.error("登陆中出现异常，msg={}", e.getMessage());
            throw new PedroAuthException(PedroAuthExceptionEnum.LOGIN_ERROR);
        }
    }

    @Override
    public void logout() {
        this.logout = true;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public boolean rememberMe() {
        return rememberMe;
    }

    @Override
    public boolean beAuthed() {
        return user != null;
    }

    @Override
    public boolean isLogout() {
        return logout;
    }

    public DefaultAuthSubject() {
    }

    public DefaultAuthSubject(User user, boolean rememberMe) {
        this.user = user;
        this.rememberMe = rememberMe;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }


    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
