package com.pedro.auth.common;

/**
 * 加密方式枚举
 */
public enum EncryptionEnum {

    /**
     * BASE64加密
     */
    BASE64_ENCRYPTION,
    /**
     * SHA加密
     */
    SHA_ENCRYPTION,
    /**
     * MD5加密
     */
    MD5_ENCRYPTION,
    /**
     * MD5盐值加密
     */
    MD5_SALT_ENCRYPTION;
}
