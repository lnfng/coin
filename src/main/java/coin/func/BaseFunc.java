package coin.func;

import java.util.Map;

public abstract class BaseFunc {
	
	/**
	 * 参数类型*/
	protected String argType;
	/**
	 * 解析到的参数
	 */
	protected Map<String, String> params;
	
	
	public abstract Map<String, String> resolveArgs(String[] args);
	
	public abstract Map<String, String> action(String[] args);
	
	
	public Map<String, String> getParams() { return params; }
	
	

}
