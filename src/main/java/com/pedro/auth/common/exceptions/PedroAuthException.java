package com.pedro.auth.common.exceptions;


import com.pedro.auth.common.enums.PedroAuthExceptionEnum;

/**
 * 服务端统一异常
 */
public final class PedroAuthException extends RuntimeException {

    /**
     * 错误码
     */
    private final String status;

    public PedroAuthException(PedroAuthExceptionEnum pedroAuthExceptionEnum) {
        // 使用父类的 message 字段
        super(pedroAuthExceptionEnum.getMsg());
        // 设置错误码
        this.status = pedroAuthExceptionEnum.getStatus();
    }

    public String getStatus() {
        return status;
    }
}
