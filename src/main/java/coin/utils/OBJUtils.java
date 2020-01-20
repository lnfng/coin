package coin.utils;

import java.util.*;

/**
 * 对象工具类
 * @author Qian
 */
public class OBJUtils {
	private OBJUtils(){}

	/**
	 * 是否是空集合
	 * @return
	 */
	public static boolean isEmptyCollection(Collection<?> cl){
		return cl==null || cl.isEmpty();
	}
	
	/**
	 * 是否是空MAP
	 * @return
	 */
	public static boolean isEmptyMap(Map<?,?> map){
		return map==null || map.isEmpty();
	}
	
	/**
	 * 是否空数组
	 * @return
	 */
	public static boolean isEmptyArray(Object[] array){
		return array == null || array.length == 0;
	}
	
	/**
	 * 是否空字符串
	 * @return
	 */
	public static boolean isBlankStr(String str){
		int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false; // 如果有非空白的则是非空
            }
        }
        return true;
	}

	/**
	 * 是否有空字符
	 * @param strs
	 * @return
	 */
	public static boolean hasBlankStr(String... strs) {
		if (strs == null || strs.length == 0) return true;
		for (String str : strs) {
			if (isBlankStr(str)) return true;
		}
		return false;
	}

	/**
	 * 不含空字符
	 * @param strs
	 * @return
	 */
	public static boolean notHasBlankStr(String... strs) {
		return !hasBlankStr(strs);
	}
	
	/**
	 * 是否非空字符串
	 * @return
	 */
	public static boolean isNotBlankStr(String str){
		return !isBlankStr(str);
	}
	
	/**
	 * 两者是否相等
	 * @return
	 */
	public static boolean isEquals(Object o1,Object o2){
		return o1 != null && o1.equals(o2);
	}
	
}
