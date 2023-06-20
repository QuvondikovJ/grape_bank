package com.example.uzum.security;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

@Component
public class EncryptAndDecrypt {

    private final String key = "This_is_my_secret_key_for_card_details_so_it_is_so_secret_1136";
    private final SecretKey secretKey;

    {
        try {
            secretKey = getSecretKeyFromMyKey(key);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private final IvParameterSpec iv = generateIv();
    private final String algorithm = "AES/CBC/PKCS5Padding";


    public SecretKey getSecretKeyFromMyKey(String mySecretKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String salt = generateSalt();
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(mySecretKey.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public String encryptCardDetails(String cardDetail) throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] cipherText = cipher.doFinal(cardDetail.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public String decryptCardDetail(String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        /* For test, When project is ready to production then please remove this code! */
        String sobirCardNumber = encryptCardDetails("1111222233334444");
        String sobirCardExpireDate = encryptCardDetails("1122");
        String farruhCardNumber = encryptCardDetails("1111222233334444");
        String farruhCardExpreDate = encryptCardDetails("1122");
        String jackCardNumber = encryptCardDetails("1111222233334444");
        String jackCardExpreDate = encryptCardDetails("1122");
        System.out.println(sobirCardNumber);
        System.out.println(sobirCardExpireDate);
        System.out.println(farruhCardNumber);
        System.out.println(farruhCardExpreDate);
        System.out.println(jackCardNumber);
        System.out.println(jackCardExpreDate);
        /*  to there!  */
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }

    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Arrays.toString(bytes);
    }


}
