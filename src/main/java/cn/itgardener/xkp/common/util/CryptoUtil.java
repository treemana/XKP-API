/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Created by zhengyi on 17-8-8 下午3:56
 */
public class CryptoUtil {
    private static final Logger logger = LoggerFactory.getLogger(CryptoUtil.class);

    private static String keyWord = "ByHunter";
    private static SecretKey secretKey;
    private static Cipher cipher;

    static {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(56, new SecureRandom(keyWord.getBytes()));
            secretKey = keyGenerator.generateKey();
            cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            logger.error("init failed : " + e.getLocalizedMessage());
        }
    }

    public static String encrypt(String source) {
        String rtv = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(keyWord.getBytes()));
            rtv = Base64.getEncoder().encodeToString(cipher.doFinal(source.getBytes()));
        } catch (Exception e) {
            logger.warn("encrypt failed : " + e.getLocalizedMessage());
        }
        return rtv;
    }

    public static String decrypt(String source) {
        String rtv = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(keyWord.getBytes()));
            rtv = new String(cipher.doFinal(Base64.getDecoder().decode(source)));
        } catch (Exception e) {
            logger.warn("decrypt failed : " + e.getLocalizedMessage());
        }
        return rtv;
    }
}
