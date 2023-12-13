package com.loopme.request;

import android.util.Base64;

import com.loopme.Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    //TODO: Consider changing encryption method
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM = "AES";

    //TODO: Check if hardcoding encryption secrets is intended
    private static final byte[] SECRET_KEY_SDK = new byte[]{
            (byte) 0xfa, (byte) 0x62, (byte) 0x44, (byte) 0xa2,
            (byte) 0x97, (byte) 0xa4, (byte) 0xba, (byte) 0x03,
            (byte) 0x2e, (byte) 0x89, (byte) 0xde, (byte) 0x9b,
            (byte) 0x77, (byte) 0xf3, (byte) 0xa2, (byte) 0xf9};

    private static SecretKeySpec secretKey;
    private static String decryptedString;
    private static String encryptedString;

    public static void setDefaultKey() {
        try {
            secretKey = new SecretKeySpec(SECRET_KEY_SDK, ALGORITHM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDecryptedString() {
        return decryptedString;
    }

    public static void setDecryptedString(String decryptedString) {
        AES.decryptedString = decryptedString;
    }

    public static String getEncryptedString() {
        return encryptedString;
    }

    public static void setEncryptedString(String encryptedString) {
        AES.encryptedString = encryptedString;
    }

    public static void encrypt(String stringToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            setEncryptedString(Base64.encodeToString(cipher.doFinal(stringToEncrypt.getBytes(Constants.UTF_8)), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void decrypt(String stringToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            setDecryptedString(new String(cipher.doFinal(Base64.decode(stringToDecrypt, Base64.DEFAULT))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
