package coin.tool;

import coin.annotation.Func;
import coin.utils.CFileUtils;
import org.reflections.Reflections;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 生成功能类列表
 * @author Qian
 * @date 2019/12/17
 */
public class GenerateFuncInfo {

    // 换行符
    private static String lineSeparator = System.getProperty("line.separator");


    /**
     * exec-maven-plugin
     * <!--程序入口，主类名称-->
     * 	<mainClass>coin.common.GenerateFuncInfo</mainClass>
     * 	<arguments>
     * 		<!--main的参数-->
     * 		<argument>coin.func</argument>
     * 		<argument>coin.annotation.Func</argument>
     * 		<argument>coin.func.base.BaseFunc</argument>
     * 	</arguments>
     * @param args
     */
    public static void main(String[] args) {
        String packagePrefix = args[0];
        String annotation = args[1];
        String baseClass = args[2];
        String filePath = Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        filePath += "META-INF";

        Reflections reflections = new Reflections(packagePrefix);
        try {
            Class<?> bclass = Class.forName(baseClass);
            Class<?> aClass = Class.forName(annotation);
            // 获取BaseFunc所有子类
            Set<Class<?>> subTypes = reflections.getSubTypesOf((Class<Object>) bclass);
            StringBuilder classNames = new StringBuilder();
            List<String> funcCodeList = new ArrayList<>();
            for (Class<?> c : subTypes) {
                if (c.isAnnotationPresent((Class<? extends Annotation>) aClass)) {
                    String[] funcCodes = c.getAnnotation(Func.class).value();
                    for (String funcCode : funcCodes) {
                        if (funcCodeList.contains(funcCode.trim())) {
                            throw new IllegalArgumentException(c.getName()+ " @Func(value=" + funcCode + ") 重复, 请重新配置");
                        }
                    }
                    funcCodeList.addAll(Arrays.asList(funcCodes));
                    classNames.append(c.getName()).append(lineSeparator);
                }
            }

            if (classNames.length() > 0) {
                // 写入文件当中
                CFileUtils.writeToFile(filePath, "func-class-list.conf", classNames.toString().getBytes());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
