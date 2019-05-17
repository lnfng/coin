package coin.comm;

import java.util.*;
import coin.utils.OBJUtils;

/**
 * 参数解析器
 * @author Qian
 */
public class ArgsResolver {

	/**
	 * 参数类型*/
	private String argType;
	/**
	 * 错误信息
	 */
	private String errMsg;
	/**
	 * 解析到的参数
	 */
	private Map<String,String> params;
	
	
	public ArgsResolver(String[] args){ 
		params = new HashMap<String,String>();
		resolve(args);
	}
	
	/**
	 * 对参数进行解析
	 * @param args
	 */
	private void resolve(String[] args){
		if(OBJUtils.isEmptyArray(args)){
			errMsg = "agrs is empty!!!";
			return;
		}
		
		// eg: argType -a1 '{"test":"123"}' -a2 hello 
		// -test '-test' 不考虑该类型的值
		
		boolean nextIsKey = true; // 
		for (String vl : args) {
			if(OBJUtils.isBlankStr(vl)) continue;
			
			vl = vl.trim();
			// 取第一个非空参数
			if(OBJUtils.isBlankStr(argType)){argType = vl;} 
			
			
			
		}
	}
	
	public Map<String, String> getParams() { return params; }
	
	public String getErrMsg() { return errMsg; }
}
