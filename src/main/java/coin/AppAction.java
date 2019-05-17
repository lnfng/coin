package coin;

import coin.annotation.Func;
import coin.func.BaseFunc;
import coin.utils.OBJUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 入口类
 * @author Qian
 */
public class AppAction {
	
	private static final Map<String,Class<BaseFunc>> FUNC_MAP = new HashMap();
	
	static{ init(); }
	
	public static void main(String[] args) {
		
		// if(OBJUtils.isEmptyArray(args)){
		// 	System.out.println(">> no args setting, nothing to do!");
		// 	return ;
		// }

		// 取第一个不为空的参数
		String funcode = null;
		for (String arg : args) {
			if(OBJUtils.isNotBlankStr(arg))
				funcode = arg;
		}
		
		funcode = "sfile";
		BaseFunc func = getFunc(funcode);
		if (func == null) {
			System.out.println(">> 没有该功能编码, 请确认再操作. funcode="+funcode);
		} else {
			func.action(args);
		}
		
	}
	
	/**
	 * 获取功能类
	 * @param funcode
	 * @return
	 */
	private static BaseFunc getFunc(String funcode){
		Class<BaseFunc> fclass = FUNC_MAP.get(funcode);
		if (fclass != null){
			try {
				return fclass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return null;
	}
	
	
	/**
	 * 初始化
	 */
	private static void init(){
		try {
			String funcpath="coin.func"; // 指定的包路径
			String cpath = AppAction.class.getResource("/").getFile();
            String funcPath = cpath + funcpath.replace(".", "/");
            File cfile = new File(funcPath);
            for (File f : cfile.listFiles()) {
            	String[] split = f.getName().split("\\.");
            	if (split.length != 2){
            		continue;
            	}
            	
            	String cname = funcpath+"."+split[0];
            	Class<?> tclass = Class.forName(cname);
            	if (tclass.isAnnotationPresent(Func.class)){
            		String funcCode = tclass.getAnnotation(Func.class).value();
            		FUNC_MAP.put(funcCode, (Class<BaseFunc>) tclass);
            	}
            }
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
