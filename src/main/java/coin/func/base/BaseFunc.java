package coin.func.base;

import java.util.Arrays;

/**
 * 功能基础类
 */
public abstract class BaseFunc {
	
	/**
	 * 功能编码*/
	private String funCode;


	/**
	 * 检查参数是否符合
	 * @param args 参数列
	 * @return
	 */
	protected abstract boolean isIllegalArgs(String[] args);

	/**
	 * 功能实现
	 * @param args 参数列
	 * @return
	 */
	protected abstract FuncResult action(String[] args);


	/**
	 * 执行
	 * @param args 参数列
	 * @return
	 */
	public FuncResult execute(String[] args) {
		if (this.isIllegalArgs(args)) {
			FuncResult result = new FuncResult();
			result.setSucc(false);
			result.setMsg("参数不符合:" + Arrays.toString(args));
			return result;
		}
		return action(args);
	}

	/**
	 * 数组参数序列成字符串
	 * @param args 参数列
	 * @return
	 */
	protected String argsToString(String[] args) {
		StringBuilder argsBuilder = new StringBuilder();
		for (String arg : args) {
			argsBuilder.append(arg).append(" ");
		}
		if (argsBuilder.length() > 1) {
			argsBuilder.deleteCharAt(argsBuilder.length() - 1);
		}
		return argsBuilder.toString();
	}

	public String getFunCode() {
		return funCode;
	}

	public void setFunCode(String funCode) {
		this.funCode = funCode;
	}
}
