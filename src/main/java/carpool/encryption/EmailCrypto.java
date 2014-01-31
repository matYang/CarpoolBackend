package carpool.encryption;

import sun.misc.*;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

import carpool.common.DebugLog;


public class EmailCrypto {
	
	
	private static final String filter = "-----##@@%%IDAUTH%%@@##-----";
    private static final String algorithm = "AES";
    private static final String plusSignPlaceHolder = "-----KINGofASEWOME---MONTEACHESYOUDECODINGISBAD-----";
	
    
    private static byte[] keyValue = "S1789JAD254BSHJ6".getBytes();

    private static String i_encrypt(String plainText) throws Exception {
            Key key = generateKey();
            Cipher chiper = Cipher.getInstance(algorithm);
            chiper.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = chiper.doFinal(plainText.getBytes());
            String encryptedValue = new BASE64Encoder().encode(encVal);
            return encryptedValue;
    }

    private static String i_decrypt(String encryptedText) throws Exception{
    		// generate key 
            Key key = generateKey();
            Cipher chiper = Cipher.getInstance(algorithm);
            chiper.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedText);
            byte[] decValue = chiper.doFinal(decordedValue);
            String decryptedValue = new String(decValue);
            return decryptedValue;
    	
    }

    private static Key generateKey() throws Exception {
            Key key = new SecretKeySpec(keyValue, algorithm);
            return key;
    }
    
    public static String encrypt(int id, String authCode){
    	try{
    		String encrypted = i_encrypt(id + filter + authCode);
        	//+ is reserved in regex, using \\ to escape it
        	//return encrypted.replace("+", plusSignPlaceHolder);
    		return java.net.URLEncoder.encode(encrypted, "utf-8");
    	} catch (Exception e){
    		DebugLog.d(e);
    		return null;
    	}
    }
    
    public static String[] decrypt(String encryptedEmailKey){
    	try{
    		encryptedEmailKey = java.net.URLDecoder.decode(encryptedEmailKey, "utf-8");
    		return i_decrypt(encryptedEmailKey.replace(plusSignPlaceHolder, "+")).split(filter);
    	} catch (Exception e){
    		DebugLog.d(e);
    		return null;
    	}
    }
	
}

/**
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.FixedStringSaltGenerator;

public class EmailCrypto {
	
	private static final String filter = "-----##@@%%IDAUTH%%@@##-----";
	private static final String salt = "fnNJonN72jJDreO2LMzR8vfeTlk5lkH";
	private static final String passphrase_version_1 = "weArenotExposinganythingInourEmails";
    private static final int iterations =20;
    private static final String algorithm = "PBEWITHSHA256AND256BITAES-CBC-BC";
    private static final String plusSignPlaceHolder = "-----KINGofASEWOME---MONTEACHESYOUDECODINGISBAD-----";
	
    private static final StandardPBEStringEncryptor emailEncrypter = new StandardPBEStringEncryptor(); 
    private static final FixedStringSaltGenerator generator = new FixedStringSaltGenerator();
    
    static{
    	emailEncrypter.setProvider(new BouncyCastleProvider());                                                                                                    
    	emailEncrypter.setAlgorithm(algorithm); 
    	
    	generator.setSalt(salt);
    	
    	emailEncrypter.setSaltGenerator(generator); 
    	emailEncrypter.setKeyObtentionIterations(iterations); 
    	emailEncrypter.setPassword(passphrase_version_1); 
    }
    
    //To avoid AlreadyInitializedException
    private static StandardPBEStringEncryptor getCrypto(){
    	return emailEncrypter;
    }
    
    private static String AESEncrypt(String plainText){
    	return getCrypto().encrypt(plainText);
    }
    
    private static String AESDecrypt(String encryptedText){
    	return getCrypto().decrypt(encryptedText);
    }
    
    //get ride of the plus signs because they are sometimes not interpreted correctly in url encoding
    public static String encrypt(int id, String authCode){
    	String encrypted = AESEncrypt(id + filter + authCode);
    	//+ is reserved in regex, using \\ to escape it
    	return encrypted.replaceAll("\\+", plusSignPlaceHolder);
    }

    public static String[] decrypt(String encryptedEmailKey){
    	return AESDecrypt(encryptedEmailKey.replace(plusSignPlaceHolder, "\\+")).split(filter);
    }
    
}
*/
