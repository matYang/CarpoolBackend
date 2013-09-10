package carpool.common;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class SessionCrypto{


	   private static final byte[] keyBytes = new byte[] {
	            0x08,0x09,0x05,0x09,0x04,0x05,0x06,0x07,0x08,0x09,
	            0x03,0x01,0x08,0x03,0x04,0x05
	        };
	
	
	   private static final byte[] ivBytes = new byte[] {
	            0x08,0x05,0x02,0x07,0x04,0x05,0x06,0x07,0x08,0x09,
	            0x00,0x03,0x06,0x03,0x04,0x05
	        };
	
	   private static Cipher cipher; 
	   private static SecretKeySpec keySpec;
	   private static IvParameterSpec ivSpec;
	   
	   private final static String HEX = "0123456789ABCDEF";
	   
	   private static void appendHex(StringBuffer sb, byte b) {
	       sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
	   }
	
	
//	
//	   public SessionCrypto() throws NoSuchAlgorithmException, NoSuchPaddingException{
//	        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
//	        keySpec = new SecretKeySpec(keyBytes, "AES"); 
//	        ivSpec = new IvParameterSpec(ivBytes);
//	   }
//	
	
	   public static String encrypt(String plainText) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException,UnsupportedEncodingException{
		   	
		    cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
	        keySpec = new SecretKeySpec(keyBytes, "AES"); 
	        ivSpec = new IvParameterSpec(ivBytes);
		   
		    byte[] plainTextBytes = plainText.getBytes("UTF8");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec); 
			byte[] encrypted = cipher.doFinal(plainTextBytes);
			return toHex(encrypted);
	
	   }
	
	
	   public static String decrypt(String cipherTextString) throws InvalidKeyException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException{
		    
		   cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
	       keySpec = new SecretKeySpec(keyBytes, "AES"); 
	       ivSpec = new IvParameterSpec(ivBytes); 
		   
		   byte[] cipherTextBytes = fromHex(cipherTextString);
		   cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		   byte[] plainTextBytes = cipher.doFinal(cipherTextBytes);
		   return new String(plainTextBytes, "UTF8");
	   }
	   
	   
	   @SuppressWarnings("unused")
	   private static String toHex(String txt) {
	       return toHex(txt.getBytes());
	   }
	   
	   private static byte[] fromHex(String hex) {
	       return toByte(hex);
	   }
	
	   private static byte[] toByte(String hexString) {
	       int len = hexString.length()/2;
	       byte[] result = new byte[len];
	       for (int i = 0; i < len; i++)
	           result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
	       return result;
	   }
	
	   private static String toHex(byte[] buf) {
	       if (buf == null)
	           return "";
	       StringBuffer result = new StringBuffer(2*buf.length);
	       for (int i = 0; i < buf.length; i++) {
	           appendHex(result, buf[i]);
	       }
	       return result.toString();
	   }
	  
}