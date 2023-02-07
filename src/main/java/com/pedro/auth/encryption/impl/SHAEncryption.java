package com.pedro.auth.encryption.impl;

import com.pedro.auth.encryption.EncryptionFacade;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA256加密
 */
public class SHAEncryption implements EncryptionFacade {

    @Override
    public String encode(String username, String password, String salt) throws Throwable {
        // 1.构造
        String str = PREFIX + username + password;
        if (!StringUtils.isEmpty(salt)) {
            str = str + salt;
        }
        // 2.加密
        return getSHA256(str);
    }

    /**
     * SHA256加密
     *
     * @param str
     * @return
     */
    public static String getSHA256(String str) throws Throwable {
        MessageDigest messageDigest;
        String encodeStr = "";
        messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(str.getBytes("UTF-8"));
        encodeStr = byte2Hex(messageDigest.digest());
        return encodeStr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuilder.append("0");
            }
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }
}
