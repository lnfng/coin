package coin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** ANNOTATION_TYPE 
	          注解类型声明 
	CONSTRUCTOR 
	          构造方法声明 
	FIELD 
	          字段声明（包括枚举常量） 
	LOCAL_VARIABLE 
	          局部变量声明 
	METHOD 
	          方法声明 
	PACKAGE 
	          包声明 
	PARAMETER 
	          参数声明 
	TYPE 
	          类、接口（包括注释类型）或枚举声明 
	
	Target注解就只有以上的类型
	当前表示可用在方法上以及类、接口（包括注释类型）或枚举上
*/
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Func {
	
	/**功能编码*/
	String value();
	
	/**
	 * 功能描述
	 * @return
	 */
	String desc() default "";

}
