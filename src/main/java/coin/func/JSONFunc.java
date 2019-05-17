package coin.func;

import java.util.Map;

import coin.annotation.Func;

/**
 * JSON 功能
 * @author Qian
 */
@Func(value="json", desc="解析json串")
public class JSONFunc extends BaseFunc {

	@Override
	public Map<String, String> resolveArgs(String[] agrs) {
		return null;
	}

	@Override
	public Map<String, String> action(String[] agrs) {
		System.out.println(">> JSONFunc");
		return null;
	}
	

	
	
}
