package com.pedro.auth.subject.impl;

import com.pedro.auth.common.EncryptionEnum;
import com.pedro.auth.model.User;
import com.pedro.auth.subject.AuthSubject;
import com.pedro.auth.subject.UserAccessFunction;

/**
 * 默认权限主体实现
 */
public class DefaultAuthSubject implements AuthSubject {

    /**
     * 用户信息
     */
    private User user;

    private boolean rememberMe = false;

    @Override
    public boolean login(String username, String password, UserAccessFunction userAccessFunction) {
        return login(username, password, null, userAccessFunction);
    }

    @Override
    public boolean login(String username, String password, EncryptionEnum encryptionType, UserAccessFunction userAccessFunction) {
        return login(username, password, null, false, userAccessFunction);
    }

    @Override
    public boolean login(String username, String password, EncryptionEnum encryptionType, boolean rememberMe, UserAccessFunction userAccessFunction) {
        // TODO
        return false;
    }

    @Override
    public void logout() {
        // TODO

    }

    @Override
    public User getUser() {
        // TODO
        return null;
    }

    @Override
    public boolean rememberMe() {
        return rememberMe;
    }

    @Override
    public boolean beAuthed() {
        return user != null;
    }

    public DefaultAuthSubject() {
    }

    public DefaultAuthSubject(User user, boolean rememberMe) {
        this.user = user;
        this.rememberMe = rememberMe;
    }

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
