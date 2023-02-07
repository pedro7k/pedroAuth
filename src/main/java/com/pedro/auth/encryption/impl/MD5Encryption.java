package com.pedro.auth.encryption.impl;

import com.pedro.auth.encryption.EncryptionFacade;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Base64;

/**
 * MD5盐值加密，默认三次
 */
public class MD5Encryption implements EncryptionFacade {

    @Override
    public String encode(String username, String password, String salt) throws Throwable {

        // 1.构造
        String str = PREFIX + username + password;
        if (!StringUtils.isEmpty(salt)) {
            str = str + salt;
        }
        // 2.加密，默认三次
        for (int i = 0; i < 3; i++) {
            str = DigestUtils.md5DigestAsHex(str.getBytes()).toUpperCase();
        }

        return str;
    }
}
