package com.pedro.auth.encryption;

/**
 * 加密工具接口
 */
public interface EncryptionFacade {

    /**
     * 密文前缀
     */
    String PREFIX = "PEDRO_AUTH";

    /**
     * 加密
     */
    String encode(String username, String password, String salt) throws Throwable;
}
