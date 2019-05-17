package coin.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * JSON 工具类
 * 传入的JSON字符格式由使用者保证,
 * 若不符合则返回NULL
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
			/* 全局修改日期格式,默认为false。JSON.DEFFAULT_DATE_FORMAT = “yyyy-MM-dd”*/
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
	 * For Excemple:<br>
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
		private int count = 1;
		
		// 目标key
		private String key;
		
		// 被查值
		private Object value;
		
		// 下一个被查值列表
		private List<Object> nextList;

		public FindAction(Object value, String key, int n){
			this.n = n;
			this.key = key;
			this.value = value;
			this.init();
		}
		
		private void init() {
			if (value instanceof String) {
				try{
					String jsonstr = ((String) value).trim();
					if (jsonstr.startsWith("{"))
						this.value = JSON.parseObject(jsonstr);
					else if (jsonstr.startsWith("["))
						this.value = JSON.parseArray(jsonstr);
				}catch(Exception e) {
					// ignore
				}
			} 
			nextList = new ArrayList<Object>(1);
			nextList.add(value);
		}
		
		/**
		 * 搜索JSON值 
		 * @param type 枚举:JSONObject|JSONArray|String
		 */
		public <T> T searchValue(Class<T> type) {
			
			List<Object> nList = new ArrayList<Object>();
			//List<Object> nList = new LinkedList<Object>();
			for (Object obj : nextList) {
				if (obj instanceof JSONObject) {
					
					JSONObject jsonObj = (JSONObject) obj;
					if (jsonObj.containsKey(key)) {
						Object target = jsonObj.get(key);
						if (type == String.class && count++ == n) {
							// 查找字符串类型
							return (T) (target!=null ? target.toString() : null);
						}
						if ((target instanceof JSONObject && type == JSONObject.class
								|| target instanceof JSONArray && type == JSONArray.class) 
								&& count++ == n) {
							// 查找JSON对象及数组类型
							return (T) target;
						}
					}
					
					for (Entry<String, Object> ent : jsonObj.entrySet()) {
						if (!(ent.getValue() instanceof String )) {
							nList.add(ent.getValue());
						}
					}
					
				} else if (obj instanceof JSONArray) {
					nList.addAll((JSONArray) obj);
				}
			}
			
			// 没匹配到则再次迭代
			if (nList.size() > 0) {
				nextList = nList;
				return searchValue(type);
			}
			
			return null;
		}
		
	}
	
	
	
	public static void main(String[] args) {

		String jsonstr = "{\"array\":[{\"test\":\"1\"},{\"obj\":{\"obj\":{\"test\":\"5\"},\"test\":\"3\"},\"test\":\"2\"},{\"obj\":{\"test123\":\"1098\"}},{\"obj\":{\"test\":\"4\"}}]}";

		System.out.println(findValue(jsonstr, "test", 1));
		System.out.println(findValue(jsonstr, "test", 2));
		System.out.println(findValue(jsonstr, "test", 3));
		System.out.println(findValue(jsonstr, "test", 4));
		
		JSONObject jsonObject = jsonToBean(jsonstr, JSONObject.class);
		System.out.println(findObject(jsonObject, "obj"));
		System.out.println(findArray(jsonObject, "array"));
		
		for(int j = 0; j < 10; j++) {
			long startTime = System.currentTimeMillis();
			for(int i = 0; i < 10*10000; i++) {
				findValue(jsonstr, "test", 3);
			}
			System.out.println(">> spend time : " + (System.currentTimeMillis() - startTime));
		}
		
	}
	
	
}
