package iot;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class AESCipher {

    String _key;
    SecretKeySpec AESkey;
    Cipher cipher;

    public AESCipher(String key) throws UnsupportedEncodingException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException {
        _key = key;
        AESkey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        cipher = javax.crypto.Cipher.getInstance("AES");
    }

    public String decrypt(String encrypted) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, this.AESkey);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException {
        AESCipher aes = new AESCipher("1234567890123456");
        String encryptstring = "yIk4/SaPv97MxWt4QurYBw==";
        String aa = aes.decrypt(encryptstring);
        System.out.println(aa);
    }
}
