package com.pedro.auth.util;

import com.pedro.auth.common.enums.EncryptionEnum;
import com.pedro.auth.encryption.EncryptionContext;
import com.pedro.auth.encryption.EncryptionFacade;
import org.springframework.lang.Nullable;

/**
 * 加密工具类
 */
public class EncryptionUtil {

    /**
     * 加密工具
     *
     * @param username       用户名
     * @param password       密码
     * @param salt           盐值，可为空
     * @param encryptionEnum 加密方式
     * @return
     * @throws Throwable
     */
    public static String encode(String username, String password,@Nullable  String salt, EncryptionEnum encryptionEnum) throws Throwable {
        EncryptionFacade encryptionTool = EncryptionContext.encryptionToolMap.get(encryptionEnum);
        return encryptionTool.encode(username, password, salt);
    }
}
