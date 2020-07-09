package coin.func.base;

/**
 * 执行结果
 * @author Qian
 * @date 2019/12/6
 */
public class FuncResult {

    // 是否成功
    private boolean isSucc;

    // 响应信息
    private String msg;

    // 其他
    private Object arg;

    public boolean isSucc() {
        return isSucc;
    }

    public void setSucc(boolean succ) {
        isSucc = succ;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getArg() {
        return arg;
    }

    public void setArg(Object arg) {
        this.arg = arg;
    }
}
