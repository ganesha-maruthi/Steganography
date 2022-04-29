package Steganography;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;

import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Encrypt {
    private static final String EncryptionAlgorithm = "AES/GCM/NoPadding";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final int TagBitLen = 128;
    public static byte[] EncryptText(String Password, String Text) {
        try {
			// IV gen
			byte iv[] = new byte[12];
			new SecureRandom().nextBytes(iv);  
			//Salt gen
			byte Salt[] = new byte[16];
			new SecureRandom().nextBytes(Salt);

			SecretKeyFactory Factory =  SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(Password.toCharArray(), Salt, 65536, 256);
			//gens AES enc 
			SecretKey secret = new SecretKeySpec(Factory.generateSecret(spec).getEncoded(), "AES");
			
			Cipher CipherObject = Cipher.getInstance(EncryptionAlgorithm);
			CipherObject.init(Cipher.ENCRYPT_MODE, secret, new GCMParameterSpec(TagBitLen, iv));
		
			byte[] PlainText = Text.getBytes(UTF_8);
			byte[] CipherText = CipherObject.doFinal(PlainText);
			byte[] FinalCipherText= ByteBuffer.allocate(iv.length + Salt.length + CipherText.length)
			.put(iv)
			.put(Salt)
			.put(CipherText)
			.array();
			return FinalCipherText;
			
        }
        catch(Exception e){

            // byte[][] var;
            // var = new byte[2][];
        	System.out.println("Error while Enc.. :" + e.toString());
            return null;
        }
    }
}
