package EncDec;
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
    public static byte[][] EncryptText(String Password,String Text){
        
        try {
        		// IV gen
	            byte iv[] = new byte[12];
	            new SecureRandom().nextBytes(iv);  
	            /*
	            System.out.println(iv);
	            System.out.println("IV vals : ");
	            for(int i=0;i<iv.length;i++){
	            	System.out.println(iv[i]);
	                }
	            */
	            //Salt gen
	            byte Salt[] = new byte[16];
	            new SecureRandom().nextBytes(Salt);
	
	            /*
	            System.out.println("Salt vals : ");
	            for(int i=0;i<Salt.length;i++){
	
	            	System.out.println(Salt[i]);
	    
	            }
	        	*/
	
	            SecretKeyFactory Factory =  SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	            KeySpec spec = new PBEKeySpec(Password.toCharArray(), Salt, 65536, 256);
	            //gens AES enc 
	            SecretKey secret = new SecretKeySpec(Factory.generateSecret(spec).getEncoded(), "AES");
	            //System.out.println("Secret 1 "+secret.getEncoded());
	            
	            Cipher CipherObject = Cipher.getInstance(EncryptionAlgorithm);
	            CipherObject.init(Cipher.ENCRYPT_MODE, secret,new GCMParameterSpec(TagBitLen, iv));
	        
		        byte[] PlainText = Text.getBytes(UTF_8);
		        byte[] CipherText = CipherObject.doFinal(PlainText);
		        byte[] FinalCipherText= ByteBuffer.allocate(iv.length + Salt.length + CipherText.length)
		        .put(iv)
		        .put(Salt)
		        .put(CipherText)
		        .array();
		        
		        
		        byte[][] Return ;
		        Return = new byte[2][];
		        Return[0] = FinalCipherText;
		        Return[1] = secret.getEncoded();
		        /*
		        System.out.println("Return [0] : "+Return[0]);
		         
		        String z = Base64.getEncoder().encodeToString(Return[0]);
		        
		        System.out.println("Z frrom Retun [0]"+z);
		     
		        */
		        
		        String x = Base64.getEncoder().encodeToString(FinalCipherText);
		        System.out.println("Cipher Text Generated :"+x);
		        return Return;
	      
        }
        catch(Exception e){

            byte[][] var;
            var = new byte[2][];
        	System.out.println("Error while Enc.. :"+e.toString());
            return var;
        }
        

    }

}
