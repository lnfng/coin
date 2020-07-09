package coin.func.base;

import coin.annotation.Func;
import coin.utils.CFileUtils;
import coin.utils.OBJUtils;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能容器类
 * @author Qian
 * @date 2019/12/17
 */
public class FuncContainer {

    private static final Map<String,Class<? extends BaseFunc>> FUNC_MAP = new HashMap();

    private FuncContainer() {}

    static {
        String cfgPath = "/META-INF/func-class-list.conf";
        InputStream inputStream = FuncContainer.class.getResourceAsStream(cfgPath);
        CFileUtils.actionRead(inputStream, (lineNo, line) -> {
            try {
                Class<?> aClass = Class.forName(line);
                if (aClass.isAnnotationPresent(Func.class)) {
                    String[] funcCodes = aClass.getAnnotation(Func.class).value();
                    for (String funcCode : funcCodes) {
                        if (OBJUtils.isBlankStr(funcCode)) continue;
                        FUNC_MAP.put(funcCode.trim(), (Class<? extends BaseFunc>) aClass);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 获取功能类
     * @param funCode
     * @return
     */
    public static BaseFunc getFunc(String funCode){
        Class<BaseFunc> fclass = (Class<BaseFunc>) FUNC_MAP.get(funCode);
        if (fclass != null){
            try {
                BaseFunc func = fclass.newInstance();
                func.setFunCode(funCode);
                return func;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
