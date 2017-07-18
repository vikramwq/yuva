package com.multitv.yuv.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by naseeb on 5/18/2017.
 */

public class SimpleCrypto {

    public static byte[] getKey() {
        return "0123456789abcdef".getBytes();
    }

    public static byte[] getIv() {
        return "fedcba9876543210".getBytes();
    }





    public static byte[] encrypt(byte[] key, byte iv[], byte[] clear)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

        IvParameterSpec ivspec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }




    public static byte[] decrypt(byte[] key, byte[] iv, byte[] encrypted)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

        IvParameterSpec ivspec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);

        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }



}
