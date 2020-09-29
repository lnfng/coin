package coin.func;

import coin.annotation.Func;
import coin.common.EnvManager;
import coin.common.EnvQueryDO;
import coin.func.base.BaseFunc;
import coin.func.base.FuncResult;
import coin.log.LogFile;
import java.util.List;
import java.util.Map;

/**
 * @author Qian
 * @date 2019/12/24
 */
@Func(value={"envA", "envB", "envC", "envD", "envS", "envT"},
        desc="envA:生产A环境, envB:生产B环境, envC:生产C环境, envD:调度环境, envS:生产商品系统, envT:测试环境")
public class EnvBaseFunc extends BaseFunc {
    // 日志文件
    private final LogFile logFile;
    // 信息管理
    private final EnvManager envManager;
    // 查询数据
    private EnvQueryDO envDO;
    // 命令参数
    private static final String cmds = "start|stop|restart|status|dfree|mfree";
    // 参数匹配规则
    private static final String args_pattern = "\\s*\\w+\\s+(" + cmds + ")\\s+(v1|v2)?\\s*(\\w+|\\w+,\\w+)+\\s*";
    // 单独重启时 参数匹配规则
    private static final String args_pattern02 = "\\s*\\w+\\s+(" + cmds + ")\\s+[\\d|.]+\\s*\\w+\\s*([\\d]+)?\\s*";


    public EnvBaseFunc() {
        logFile = LogFile.getInstance(this.getClass());
        envManager = new EnvManager();
    }

    @Override
    protected boolean isIllegalArgs(String[] args) {
        String argsStr = this.argsToString(args);
        logFile.log2File("[本次执行] : " + argsStr);

        // 不匹配则是非法参数
        String funCode = this.getFunCode();
        if (!(argsStr.matches(args_pattern) || argsStr.matches(args_pattern02))
                || (args[2].matches("[\\d|.]+") && args.length < 4)) {
            System.out.println(">>");
            System.out.println(">> 参数:"+argsStr+"不匹配!");
            System.out.println(">> eg: " + funCode + " " + cmds + " v1/v2(空则v1&v2) server_names(多个逗号隔开)");
            System.out.println(">> eg: " + funCode + " status v1 app,server");
            System.out.println(">> eg: " + funCode + " " + cmds + " IP地址 server_name(单个) 01(节点编号空则该ip所有节点)");
            System.out.println(">> eg: " + funCode + " status 10.123.100.36 app 01");
            System.out.println(">>");
            return true;
        }
        // 解析参数
        envDO = new EnvQueryDO();
        envDO.setActCode(args[1]);
        envDO.setEnvCode(funCode.replace("env", ""));
        if (args[2].matches("[\\d|.]+")) {
            envDO.setIp(args[2]);
            envDO.setServerNames(args[3]);
            envDO.setNode(args.length == 5 ? args[4]: null);
        } else {
            envDO.setServerNames(args[args.length-1]);
            envDO.setVersion(args.length == 4 ? args[2]: null);
        }
        return false;
    }

    @Override
    protected FuncResult action(String[] args) {
        FuncResult result = new FuncResult();
        if (!envManager.checkActAuthority(this.getFunCode(), envDO.getActCode())) {
            result.setMsg("没有赋予:" + this.getFunCode() + "的" + envDO.getActCode() + "的权限, 请检查是否在正确的环境运行!");
            System.out.println(">> ");
            System.out.println(">> " + result.getMsg());
            System.out.println(">> ");
            logFile.log2File(result.getMsg());
            return result;
        }

        // 获取服务配置信息
        List<Map> serverInfos = envManager.getServerInfos(envDO);
        if (serverInfos.size() == 0) {
            result.setMsg("数据库未配置服务信息!");
            System.out.println(">> ");
            System.out.println(">> " + result.getMsg());
            System.out.println(">> ");
            logFile.log2File(result.getMsg());
            return result;
        }

        // 执行操作
        String log = envManager.doAction(envDO.getActCode(), serverInfos);
        logFile.log2File(log);

        result.setMsg("执行完毕!!!");
        result.setSucc(true);
        System.out.println(">> " + result.getMsg());
        return result;
    }
}
