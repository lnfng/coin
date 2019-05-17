package coin.func;

import java.io.*;
import java.util.*;
import coin.annotation.Func;
import coin.utils.OBJUtils;
import coin.utils.CSecurityUtils;

@Func("sfile")
public class SafeFileFunc extends BaseFunc {
	
	private static final String APPEND_KEY = "*";

	@Override
	public Map<String, String> resolveArgs(String[] agrs) {
		return null;
	}

	@Override
	public Map<String, String> action(String[] agrs) {
		
		decodeFile();
		
		if(1==1) return null;
		
		String passwd = "729452";
		String key = getKey(passwd);
		
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		String fpath = "G:/Documents/PrivateManager/Major/holmes";
		String outpath = "G:/Documents/PrivateManager/Major/enholmes";
		try {
			inputStream = new FileInputStream(fpath);
			outputStream = new FileOutputStream(outpath);
			
			byte[] buf = new byte[16*1024];
			for(int len = 0; (len = inputStream.read(buf)) != -1; ){
				if(len != buf.length){
					byte[] dest = new byte[len];
					System.arraycopy(buf, 0, dest, 0, len);
					buf = dest;
				}
				String enStr = CSecurityUtils.encrypt(key, buf);
				outputStream.write(enStr.getBytes());
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					// ignore
				}
			if (outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e) {
					// ignore
				}
		}
		return null;
	}
	
	private void decodeFile(){
		String passwd = "729452";
		String key = getKey(passwd);
		
		FileInputStream inputStream = null;
		String fpath = "G:/Documents/PrivateManager/Major/enholmes";
		try {
			inputStream = new FileInputStream(fpath);
			
			byte[] buf = new byte[16*1024];
			for(int len = 0; (len = inputStream.read(buf)) != -1; ){
				if(len != buf.length){
					byte[] dest = new byte[len];
					System.arraycopy(buf, 0, dest, 0, len);
					buf = dest;
				}
				String enStr = CSecurityUtils.decrypt(key, buf);
				System.out.println(enStr);
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					// ignore
				}
		}
		
	}
	
	
	/**
	 * 返回16位的秘钥
	 * @param passwd
	 * @return
	 */
	private String getKey(String passwd){
		String key = OBJUtils.isBlankStr(passwd) ? "" : passwd;
		StringBuilder builder = new StringBuilder(key);
		for (int i = (16-key.length()); i > 0; i--) {
			builder.append(APPEND_KEY);
		}
		return builder.toString();
	}
	
	
	
	

}
