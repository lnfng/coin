package coin.func;

import coin.annotation.Func;
import coin.func.base.BaseFunc;
import coin.func.base.FuncResult;
import coin.utils.CJSONUtils;

/**
 * JSON 功能
 * @author Qian
 */
@Func(value="json", desc="解析json串")
public class JSONFunc extends BaseFunc {
    // 参数匹配规则
    private static final String args_pattern = "\\s*\\w+\\s+(-k)\\s+\\w+\\s+(-n\\s+\\d+\\s+)?[{|\\[][\\W\\w]*[}|\\]]\\s*";


    @Override
    protected boolean isIllegalArgs(String[] args) {
        String argStr = this.argsToString(args);
        if (!argStr.matches(args_pattern)) {
            System.out.println(">> 参数不正确 : " + argStr);
            System.out.println("eg: json -k obj {\"array\":[1,2], \"obj\":{\"name\":\"hello\"}}");
            System.out.println("eg: json -k obj -n 2 {\"array\":[1,2], \"obj\":{\"obj\":\"hello\"}}");
            return true;
        }
        return false;
    }


    @Override
    protected FuncResult action(String[] args) {
        String key = args[2];
        int no = "-n".equals(args[3]) ? Integer.parseInt(args[4]) : 1;
        StringBuilder jsonBuilder = new StringBuilder();
        for (int i = "-n".equals(args[3]) ? 5 : 3; i < args.length; i++) {
            jsonBuilder.append(args[i]);
        }
        String value = CJSONUtils.findValue(jsonBuilder.toString(), key, no);
        System.out.println(value);
        FuncResult result = new FuncResult();
        result.setSucc(true);
        result.setMsg(value);
        return result;
    }


}
