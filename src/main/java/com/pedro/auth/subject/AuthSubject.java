package com.pedro.auth.subject;

import com.pedro.auth.common.enums.EncryptionEnum;
import com.pedro.auth.model.User;

/**
 * 权限管理主体
 */
public interface AuthSubject {

    /**
     * @param username           用户名
     * @param password           密码
     * @param userAccessFunction 用户信息获取方法
     * @return 登陆是否成功
     */
    boolean login(String username, String password, UserAccessFunction userAccessFunction);

    /**
     * @param username           用户名
     * @param password           密码
     * @param encryptionType     加密方式
     * @param userAccessFunction 用户信息获取方法
     * @return 登陆是否成功
     */
    boolean login(String username, String password, EncryptionEnum encryptionType, UserAccessFunction userAccessFunction);

    /**
     * @param username           用户名
     * @param password           密码
     * @param rememberMe         是否记住我
     * @param userAccessFunction 用户信息获取方法
     * @return 登陆是否成功
     */
    boolean login(String username, String password, boolean rememberMe, UserAccessFunction userAccessFunction);

    /**
     * @param username           用户名
     * @param password           密码
     * @param encryptionType     加密方式
     * @param rememberMe         是否记住我
     * @param userAccessFunction 用户信息获取方法
     * @return 登陆是否成功
     */
    boolean login(String username, String password, EncryptionEnum encryptionType, boolean rememberMe, UserAccessFunction userAccessFunction);

    /**
     * 注销
     */
    void logout();

    /**
     * 设置当前用户
     */
    void setUser(User user);

    /**
     * 获得当前用户
     */
    User getUser();

    /**
     * 是否有记住我，仅在登录请求中有用
     */
    boolean rememberMe();

    /**
     * 是否已经认证
     */
    boolean beAuthed();

    /**
     * 是否是注销，用于后置处理中清除session、cookie
     */
    boolean isLogout();

}
