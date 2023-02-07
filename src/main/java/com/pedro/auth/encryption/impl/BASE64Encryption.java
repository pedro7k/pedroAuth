package com.pedro.auth.encryption.impl;

import com.pedro.auth.encryption.EncryptionFacade;
import org.springframework.util.StringUtils;

import java.util.Base64;

/**
 * BASE64
 */
public class BASE64Encryption implements EncryptionFacade {

    @Override
    public String encode(String username, String password, String salt) throws Throwable{

        // 1.构造
        String str = PREFIX + username + password;
        if (!StringUtils.isEmpty(salt)) {
            str = str + salt;
        }

        // 2.加密
        return Base64.getEncoder().withoutPadding().encodeToString(str.getBytes());
    }
}
