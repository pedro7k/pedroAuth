package com.pedro.auth.subject;

import com.pedro.auth.common.EncryptionEnum;
import com.pedro.auth.model.User;

/**
 * 权限管理主体
 */
public interface AuthSubject {

    /**
     *
     * @param username 用户名
     * @param password 密码
     * @param userAccessFunction 用户信息获取方法
     * @return 登陆是否成功
     */
    boolean login(String username, String password, UserAccessFunction userAccessFunction);

    /**
     *
     * @param username 用户名
     * @param password 密码
     * @param encryptionType 加密方式
     * @param userAccessFunction 用户信息获取方法
     * @return 登陆是否成功
     */
    boolean login(String username, String password, EncryptionEnum encryptionType, UserAccessFunction userAccessFunction);

    /**
     *
     * @param username 用户名
     * @param password 密码
     * @param encryptionType 加密方式
     * @param rememberMe 是否记住我
     * @param userAccessFunction 用户信息获取方法
     * @return 登陆是否成功
     */
    boolean login(String username, String password, EncryptionEnum encryptionType, boolean rememberMe, UserAccessFunction userAccessFunction);

    /**
     * 注销
     */
    void logout();

    /**
     * 获得当前用户
     */
    User getUser();

    /**
     * 是否有记住我
     */
    boolean rememberMe();

    /**
     * 是否认证
     */
    boolean beAuthed();

}
