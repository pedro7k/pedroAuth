package com.pedro.auth.model;

import java.util.List;

/**
 * 用户实例
 */
public class User {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 权限列表
     */
    private List<String> permissionList;

    /**
     * md5盐值加密-盐值
     */
    private String salt;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, List<String> permissionList) {
        this.username = username;
        this.password = password;
        this.permissionList = permissionList;
    }

    public User(String username, String password, List<String> permissionList, String salt) {
        this.username = username;
        this.password = password;
        this.permissionList = permissionList;
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
