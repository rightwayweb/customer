package com.zitego.customer.creditCard;

import com.zitego.util.Hex;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class will encrypt data given a secret password using the cryptix java classes.
 *
 * @author John Glorioso
 * @version $Id: CcEncryption.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class CcEncryption
{
    private static final String PRE_KEY = "J$#9";
    private static final String POST_KEY = "~i*3#";

    public static void main(String[] args) throws Exception
    {
        String data = args[0];
        String key = args[1];
        System.out.println("data: "+data);
        System.out.println("key: "+key);
        String encryptedData = encryptDataToHex(data, key);
        System.out.println("encryptedData: "+encryptedData);
        String decryptedData = decryptDataFromHex(encryptedData, key);
        System.out.println("decryptedData :"+ decryptedData);
    }

    /**
     * Encrypts the given data.
     *
     * @param data The data.
     * @param key The secret key.
     * @return byte[]
     * @throws Exception if an error occurs encrypting the data.
     */
    public static byte[] encryptData(String data, String key) throws Exception
    {
        SecretKeySpec keySpec = new SecretKeySpec(new String(PRE_KEY+key+POST_KEY).getBytes(), "BLOWFISH");
        Cipher cipher = Cipher.getInstance("BLOWFISH");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal( data.getBytes() );
    }

    /**
     * Encrypts the given data to a hex string.
     *
     * @param data The data.
     * @param key The secret key.
     * @return String
     * @throws Exception if an error occurs encrypting the data.
     */
    public static String encryptDataToHex(String data, String key) throws Exception
    {
        return Hex.encode( encryptData(data, key) );
    }

    /**
     * Decrypts the given data.
     *
     * @param data The data.
     * @param key The secret key.
     * @throws Exception if an error occurs encrypting the data.
     */
    public static String decryptData(byte[] data, String key) throws Exception
    {
        SecretKeySpec keySpec = new SecretKeySpec(new String(PRE_KEY+key+POST_KEY).getBytes(), "BLOWFISH");
        Cipher cipher = Cipher.getInstance("BLOWFISH");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return new String( cipher.doFinal(data) );
    }

    /**
     * Decrypts the given data from a hex string.
     *
     * @param data The data.
     * @param key The secret key.
     * @throws Exception if an error occurs encrypting the data.
     */
    public static String decryptDataFromHex(String data, String key) throws Exception
    {
        return decryptData(Hex.decodeToByteArray(data.toCharArray()), key);
    }
}