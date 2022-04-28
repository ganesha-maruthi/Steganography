package Steganography;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;

import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Decrypt {
    private static final String EncryptionAlgorithm = "AES/GCM/NoPadding";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final int TagBitLen = 128;
    public static void DecryptText(byte[] CipherText,String UserName,String Password){
        try {
            ByteBuffer bb = ByteBuffer.wrap(CipherText);
            byte[] RecvIv = new byte[12];
            bb.get(RecvIv);
    
            byte[] RecvSalt = new byte[16];
            bb.get(RecvSalt);
    
            byte[] RecvCipherText = new byte[bb.remaining()];
            bb.get(RecvCipherText);
        
            SecretKeyFactory Factory2 =  SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec2 = new PBEKeySpec(Password.toCharArray(), RecvSalt, 65536, 256);
           
            SecretKey secret2 = new SecretKeySpec(Factory2.generateSecret(spec2).getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance(EncryptionAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, secret2, new GCMParameterSpec(TagBitLen, RecvIv));
    
            byte[] plainText = cipher.doFinal(RecvCipherText);
            String OP= new String(plainText, UTF_8);
            System.out.println("Decrypted Cipher Text : "+OP);
        }
        catch(Exception e){
            System.out.println("Error while Dec.. :"+e.toString());
        }
   } 
}
