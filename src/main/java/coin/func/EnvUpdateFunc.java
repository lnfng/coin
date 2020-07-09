package coin.func;

import coin.annotation.Func;
import coin.common.EnvManager;
import coin.func.base.BaseFunc;
import coin.func.base.FuncResult;
import coin.utils.CFileUtils;
import coin.utils.CommUtils;
import com.alibaba.fastjson.JSON;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 运行环境信息更新功能
 * @author Qian
 */
@Func(value="envU", desc="运行环境信息更新功能")
public class EnvUpdateFunc extends BaseFunc {
    // 参数匹配规则
    private static final String args_pattern = "\\s*envU\\s*";

    @Override
    protected boolean isIllegalArgs(String[] args) {
        String argStr = this.argsToString(args);
        if (!argStr.matches(args_pattern)) {
            System.out.println(">> 参数不正确 : " + argStr);
            return true;
        }
        return false;
    }


    @Override
    protected FuncResult action(String[] args) {
        EnvManager envManager = new EnvManager();
        List<Map> infos = envManager.getServerInfos();
        if (infos != null && infos.size() > 0) {
            String path = CommUtils.getDeployPath() + "/config";
            String fileName = "env.infos.json";
            String jsonString = JSON.toJSONString(infos);
            CFileUtils.writeToFile(path, fileName, jsonString.getBytes(StandardCharsets.UTF_8));
        }
        System.out.println(">> 数据更新完毕!!!");
        return new FuncResult();
    }


}
