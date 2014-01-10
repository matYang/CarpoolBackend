package carpool.encryption;

import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;

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
    private static boolean hasInitialized = false;
    
    //To avoid AlreadyInitializedException
    private static StandardPBEStringEncryptor getCrypto(){
    	if (!hasInitialized){
    		emailEncrypter.setProvider(new BouncyCastleProvider());                                                                                                    
        	emailEncrypter.setAlgorithm(algorithm); 
        	
        	generator.setSalt(salt);
        	
        	emailEncrypter.setSaltGenerator(generator); 
        	emailEncrypter.setKeyObtentionIterations(iterations); 
        	emailEncrypter.setPassword(passphrase_version_1); 
        	
        	hasInitialized = true;
    	}
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
