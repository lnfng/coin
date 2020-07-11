package coin.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.*;
import java.util.Map.Entry;

/**
 * JSON 工具类
 * @author Qian
 */
public class CJSONUtils {

	private CJSONUtils(){}
	
	/**
	 * 将对象转换成JSON字符串
	 * @param obj 待转换对象
	 * @return JSON串
	 */
	public static String toJSONStr(Object obj){
		if (obj == null) {
			return null;
		}
		
		SerializerFeature[] features = { 
			/* 输出空置字段*/	
			SerializerFeature.WriteMapNullValue,
			/* list字段如果为null，输出为[]，而不是null*/
			SerializerFeature.WriteNullListAsEmpty, 
			/* 字符类型字段如果为null，输出为""，而不是null*/
			SerializerFeature.WriteNullStringAsEmpty,
			/* 全局修改日期格式,默认为false。
			JSON.DEFFAULT_DATE_FORMAT = “yyyy-MM-dd”*/
			SerializerFeature.WriteDateUseDateFormat, 
			/* 消除对同一对象循环引用的问题，默认为false*/
			SerializerFeature.DisableCircularReferenceDetect 
		};
		/* 设置日期格式 */
		JSON.DEFFAULT_DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
		return JSON.toJSONString(obj, features);
	}
	
	/**
	 * 将JSON字符串转换成Bean对象
	 * For Example:<br>
	 * 	Person person = CJSONUtils.jsonToBean(jsonstr, Person.class);<br>
	 * 	JSONObject jsonObject = CJSONUtils.jsonToBean(jsonstr, JSONObject.class);<br>
	 * @param jsonstr JSON串
	 * @param clazz Bean类型
	 * @return Bean对象
	 */
	public static <T> T jsonToBean(String jsonstr,Class<T> clazz){
		if (jsonstr == null || clazz == null) {
			return null;
		}
		T obj = null;
		try {
			obj = JSONObject.parseObject(jsonstr, clazz);
		} catch (Exception e) {
			// ignore
		}
		return obj;
	}

	
	/**
	 * 查找JSON值
	 * @param jsonstr 原JSON串
	 * @param key KEY值
	 * @return 返回第一个搜索到的值(按对象层级由外及内)
	 */
	public static String findValue(String jsonstr,String key){
		return findValue(jsonstr, key, 1);
	}
	
	
	/**
	 * 查找JSON值
	 * @param jsonstr 原JSON串
	 * @param key KEY值
	 * @param n 该KEY第n(n>=1)次出现
	 * @return 返回第n次搜索到的值(按对象层级由外及内)
	 */
	public static String findValue(String jsonstr, String key, int n){
		if (jsonstr == null || key == null || n<1) {
			return null;
		}
		return new FindAction(jsonstr,key,n).searchValue(String.class);
	}
	
	/**
	 * 查找JSON值
	 * @param jsonObject JSON对象
	 * @param key KEY值
	 * @return 返回第一个搜索到的值(按对象层级由外及内)
	 */
	public static String findValue(JSONObject jsonObject,String key){
		return findValue(jsonObject, key, 1);
	}
	
	/**
	 * 查找JSON值
	 * @param jsonObject JSON对象
	 * @param key KEY值
	 * @param n 该KEY第n(n>=1)次出现
	 * @return 返回第n次搜索到的值(按对象层级由外及内)
	 */
	public static String findValue(JSONObject jsonObject, String key, int n){
		if (jsonObject == null || key == null || n < 1) {
			return null;
		}
		return new FindAction(jsonObject,key,n).searchValue(String.class);
	}
	
	
	/**
	 * 查找JSON对象
	 * @param jsonstr 原JSON串
	 * @param key KEY值
	 * @return 返回第1次搜索到的值(按对象层级由外及内)
	 */
	public static JSONObject findObject(String jsonstr, String key){
		return findObject(jsonstr, key, 1);
	}
	
	
	/**
	 * 查找JSON对象
	 * @param jsonstr 原JSON串
	 * @param key KEY值
	 * @param n 该KEY第n(n>=1)次出现
	 * @return 返回第n次搜索到的值(按对象层级由外及内)
	 */
	public static JSONObject findObject(String jsonstr, String key, int n){
		if (jsonstr == null || key == null || n < 1) {
			return null;
		}
		return new FindAction(jsonstr,key,n).searchValue(JSONObject.class);
	}
	
	
	/**
	 * 查找JSON对象
	 * @param jsonObject 原JSON对象
	 * @param key KEY值
	 * @return 返回第1次搜索到的值(按对象层级由外及内)
	 */
	public static JSONObject findObject(JSONObject jsonObject, String key){
		return findObject(jsonObject, key, 1);
	}
	
	/**
	 * 查找JSON对象
	 * @param jsonObject 原JSON对象
	 * @param key KEY值
	 * @param n 该KEY第n(n>=1)次出现
	 * @return 返回第n次搜索到的值(按对象层级由外及内)
	 */
	public static JSONObject findObject(JSONObject jsonObject, String key, int n){
		if (jsonObject == null || key == null || n < 1) {
			return null;
		}
		return new FindAction(jsonObject,key,n).searchValue(JSONObject.class);
	}
	
	
	/**
	 * 查找JSON数组
	 * @param jsonstr 原JSON串
	 * @param key KEY值
	 * @return 返回第1次搜索到的值(按对象层级由外及内)
	 */
	public static JSONArray findArray(String jsonstr, String key){
		return findArray(jsonstr, key, 1);
	}
	
	/**
	 * 查找JSON数组
	 * @param jsonstr 原JSON串
	 * @param key KEY值
	 * @param n 该KEY第n(n>=1)次出现
	 * @return 返回第n次搜索到的值(按对象层级由外及内)
	 */
	public static JSONArray findArray(String jsonstr, String key, int n){
		if (jsonstr == null || key == null || n < 1) {
			return null;
		}
		return new FindAction(jsonstr,key,n).searchValue(JSONArray.class);
	}
	
	/**
	 * 查找JSON数组
	 * @param jsonObject 原JSON对象
	 * @param key KEY值
	 * @return 返回第1次搜索到的值(按对象层级由外及内)
	 */
	public static JSONArray findArray(JSONObject jsonObject, String key){
		return findArray(jsonObject, key, 1);
	}
	
	/**
	 * 查找JSON数组
	 * @param jsonObject 原JSON对象
	 * @param key KEY值
	 * @param n 该KEY第n(n>=1)次出现
	 * @return 返回第n次搜索到的值(按对象层级由外及内)
	 */
	public static JSONArray findArray(JSONObject jsonObject, String key, int n){
		if (jsonObject == null || key == null || n < 1) {
			return null;
		}
		return new FindAction(jsonObject,key,n).searchValue(JSONArray.class);
	}
	
	
	/**
	 * 查找类
	 * @author Qian
	 */
	private static class FindAction {
		// 该KEY第n(n>=1)次出现
		private int n;
		
		// 匹配到的次数
		private int count = 0;
		
		// 目标key
		private String key;
		
		// 被查值
		private Object rawValue;
		
		// 下一个被查值列表
		private List<Object> nextList;

		FindAction(Object rawValue, String key, int n){
			this.n = n;
			this.key = key;
			this.rawValue = rawValue;
			this.init();
		}
		
		private void init() {
			if (rawValue instanceof String) {
				try{
					String jsonstr = ((String) rawValue).trim();
					if (jsonstr.startsWith("{"))
						this.rawValue = JSON.parseObject(jsonstr);
					else if (jsonstr.startsWith("["))
						this.rawValue = JSON.parseArray(jsonstr);
				}catch(Exception e) {
					// ignore
				}
			} 
			nextList = Collections.singletonList(rawValue);
		}
		
		/**
		 * 搜索JSON值 
		 * @param type 枚举:JSONObject|JSONArray|String
		 */
		<T> T searchValue(Class<T> type) {

			List<Object> nList = new ArrayList<Object>();
			for (Object obj : nextList) {
				if (obj instanceof JSONObject) {

					JSONObject jsonObj = (JSONObject) obj;
					if (jsonObj.containsKey(key) && (++count) == n) {
						Object target = jsonObj.get(key);
						if (target == null) {
						    return null;
                        }
						if (type == String.class) {
							// 查找字符串类型
							return  (T) target.toString();
						}
						if (type == JSONObject.class &&  target instanceof JSONObject
                                || type == JSONArray.class && target instanceof JSONArray) {
							return (T) target;
						}
					}
					// 不匹配则取下一层
					nList.addAll(jsonObj.values());

				} else if (obj instanceof JSONArray) {
					nList.addAll((JSONArray) obj);
				}
			}

			// 没匹配到则再次迭代
			nextList = nList;
			return nextList.size() > 0 ? searchValue(type) : null;
		}
		
	}
	
	
	
	public static void main(String[] args) {

		String jsonstr = "{\"array\":[{\"test\":\"1\"},{\"obj\":{\"obj\":{\"test\":\"5\"},\"test\":\"3\"},\"test\":\"2\"},{\"obj\":{\"test123\":\"1098\"}},{\"obj\":{\"test\":\"4\"}}]}";

		System.out.println(findValue(jsonstr, "test", 1));
		System.out.println(findValue(jsonstr, "test", 2));
		System.out.println(findValue(jsonstr, "test", 3));
		System.out.println(findValue(jsonstr, "test", 4));
		System.out.println(findValue(jsonstr, "test", 5));

		JSONObject jsonObject = jsonToBean(jsonstr, JSONObject.class);
		System.out.println(findObject(jsonObject, "obj"));
		System.out.println(findArray(jsonObject, "array"));

		for(int j = 0; j < 10; j++) {
			long startTime = System.currentTimeMillis();
			for(int i = 0; i < 10*10000; i++) {
				findValue(jsonstr, "cust_name", 3);
			}
			System.out.println(">> spend time : " + (System.currentTimeMillis() - startTime));
		}
		
	}
	
	
}
