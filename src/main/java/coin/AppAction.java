package coin;

import coin.func.base.FuncContainer;
import coin.func.base.BaseFunc;
import coin.utils.OBJUtils;

/**
 * 程序执行入口类
 * @author Qian
 */
public class AppAction {
	

	/**
	 * 程序执行入口
	 * @param args
	 */
	public static void main(String[] args) {
		if(OBJUtils.isEmptyArray(args)){
			System.out.println(">>");
			System.out.println(">> 参数为空, 没有任何目标被执行!");
			System.out.println(">>");
			return ;
		}

		// 简单处理参数
		String funCode = args[0];
		// 获取功能类
		BaseFunc func = FuncContainer.getFunc(funCode);
		if (func == null) {
			System.out.println(">>");
			System.out.println(">> 没有该目标功能编码, 请确认再操作:"+funCode);
			System.out.println(">>");
		} else {
			func.execute(args);
		}

	}

}
