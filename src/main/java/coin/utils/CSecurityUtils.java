package coin.utils;

import java.util.Date;
import java.util.UUID;
import java.text.SimpleDateFormat;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * 安全工具类
 * @author Qian
 */
public class CSecurityUtils {
	
	public static final String DATE_FOMAT_1 = "yyyyMMddHHmmssSSS";
	public static final String DATE_FOMAT_2 = "yyyy-MM-dd HH:mm:ss.SSS";
	
	public static final String CIPHER_ALGORITHM = "AES";
    public static final String CIPHER_ALGORITHM_INS = "AES/ECB/PKCS5Padding";
    public static final String ENCODE = "UTF-8";
	
	/**
	 * 获取时间戳
	 * @param format 时间戳格式
	 * @return
	 */
	public static String getTimestamp(String format){
		return new SimpleDateFormat(format).format(new Date());
	}
	
	/**
	 * 获取UUID
	 * @return
	 */
	public static String getUUID(){
		return String.valueOf(UUID.randomUUID());
	}
	
	/**
     * AES加密
     * @param key 16位长度密钥
     * @param data 原始数据
     */
    public static String encrypt(String key,byte[] data) {
    	try{
    		SecretKey secretKey = new SecretKeySpec(key.getBytes(ENCODE), CIPHER_ALGORITHM); // 恢复密钥
    		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_INS);	// Cipher完成加密或解密工作类
    		cipher.init(Cipher.ENCRYPT_MODE, secretKey);				// 对Cipher初始化，解密模式
    		byte[] cipherByte = cipher.doFinal(data);// 加密data
    		return  Base64.encode(cipherByte);
    	}catch(Exception e){
    		throw new RuntimeException(e);
    	}
        
    }
    /**
     * AES解密
     * @param key 16位长度密钥
     * @param data 加密数据
     */
    public static String decrypt(String key,byte[] data) {
    	try{
	    	SecretKey secretKey = new SecretKeySpec(key.getBytes(ENCODE), CIPHER_ALGORITHM); // 恢复密钥
	        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_INS); 	// Cipher完成加密或解密工作类
	        cipher.init(Cipher.DECRYPT_MODE, secretKey); 				// 对Cipher初始化，解密模式
	        byte[] cipherByte = cipher.doFinal(Base64.decode(data, 0, data.length)); // 解密data
	        return new String(cipherByte,ENCODE);
	    }catch(Exception e){
			throw new RuntimeException(e);
		}
    }
    
    
    public static void main(String[] args) {
		String dataStr = "Hello World";
		String key = "000000000000abcd";
		String enStr = encrypt(key, dataStr.getBytes());
		System.out.println(enStr);
		System.out.println(decrypt(key, enStr.getBytes()));
		
	}
	

}
