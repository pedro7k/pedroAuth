package com.pedro.auth.encryption;

import com.pedro.auth.common.enums.EncryptionEnum;
import com.pedro.auth.encryption.impl.BASE64Encryption;
import com.pedro.auth.encryption.impl.MD5Encryption;
import com.pedro.auth.encryption.impl.SHAEncryption;

import java.util.HashMap;
import java.util.Map;

/**
 * 加密上下文
 */
public class EncryptionContext {

    public static Map<EncryptionEnum, EncryptionFacade> encryptionToolMap = new HashMap<>();

    static {
        encryptionToolMap.put(EncryptionEnum.BASE64_ENCRYPTION, new BASE64Encryption());
        encryptionToolMap.put(EncryptionEnum.SHA_ENCRYPTION, new SHAEncryption());
        encryptionToolMap.put(EncryptionEnum.MD5_ENCRYPTION, new MD5Encryption());
    }
}
