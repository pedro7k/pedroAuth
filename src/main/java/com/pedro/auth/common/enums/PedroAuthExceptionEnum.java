package com.pedro.auth.common.enums;

// ServiceExceptionEnum.java

/**
 * 返回状态枚举
 */
public enum PedroAuthExceptionEnum {

    /**
     * 错误状态和信息枚举
     */
    PEDRO_AUTH_ERROR("0", "[pedroAuth]权限管理出现异常"),
    LOGIN_ERROR("1", "[pedroAuth]登录过程中出现异常"),
    USERNAME_ERROR("2", "[pedroAuth]登录用户名错误"),
    PASSWORD_ERROR("3", "[pedroAuth]登录密码错误"),
    GET_REQUEST_INFO_ERROR("4", "[pedroAuth]获取请求request/response信息失败");

    /**
     * 错误码
     */
    private String status;
    /**
     * 错误提示
     */
    private String msg;

    PedroAuthExceptionEnum(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}