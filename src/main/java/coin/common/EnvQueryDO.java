package coin.common;

import coin.utils.OBJUtils;

import java.util.Map;

/**
 * @author Qian
 * @date 2020/1/3
 */
public class EnvQueryDO {

    private String
            actCode,     // 操作编码
            version,     // 1.0环境 或者 2.0 环境
            serverNames, // 服务名称
            envCode,     // 环境编码
            ip,          // ip地址
            node;        // 节点编码


    /**
     * 数据是否匹配
     * @param data 环境信息
     * @return
     */
    public boolean isMatch(Map<String, String> data) {
        if (data == null) return false;

        boolean expect = true;
        if (OBJUtils.isNotBlankStr(envCode)) {
            expect = envCode.equals(data.get("env_code"));
        }
        if (OBJUtils.isNotBlankStr(serverNames) && expect) {
            expect = serverNames.contains(data.get("server_name"));
        }
        if (OBJUtils.isNotBlankStr(version) && expect) {
            String sversion = "v1".equals(version) ? "1.0" : "2.0";
            expect = sversion.equals(data.get("sversion"));
        }
        if (OBJUtils.isNotBlankStr(ip) && expect) {
            expect = ip.equals(data.get("ip"));
        }
        if (OBJUtils.isNotBlankStr(node) && expect) {
            expect = node.equals(data.get("node"));
        }

        return expect;
    }


    public String getActCode() {
        return actCode;
    }

    public void setActCode(String actCode) {
        this.actCode = actCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServerNames() {
        return serverNames;
    }

    public void setServerNames(String serverNames) {
        this.serverNames = serverNames;
    }

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }
}
