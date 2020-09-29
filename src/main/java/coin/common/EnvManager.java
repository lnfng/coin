package coin.common;

import coin.utils.CFileUtils;
import coin.utils.CommUtils;
import coin.utils.DBOperator;
import coin.utils.OBJUtils;
import coin.utils.terminal.ShellInstance;
import coin.utils.terminal.SshSessionContainer;
import coin.utils.terminal.SshUserInfo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Qian
 * @date 2019/12/28
 */
public class EnvManager {

    // 换行符
    private static final  String lineSeparator = System.getProperty("line.separator");
    // 数据库操作类
    private final DBOperator operator = DBOperator.getDBOperator(DBOperator.Env.TEST);
    // table 键
    private static final String tab = "\t";




    /**
     * 检查执行权限
     * @param funCode
     * @param actCode
     * @return
     */
    public boolean checkActAuthority(String funCode, String actCode) {
        try {
            InputStream inputStream;
            File cfgFile = new File(CommUtils.getDeployPath() + "/config/env.act.authority.properties");
            if (cfgFile.exists()) {
                inputStream = new FileInputStream(cfgFile);
            } else {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                inputStream = loader.getResourceAsStream("coin/resources/env.act.authority.properties");
            }
            Properties actAuthority = new Properties();
            actAuthority.load(inputStream);
            String actList = actAuthority.getProperty(funCode + ".act.list", "status");
            return Arrays.asList(actList.split(",")).contains(actCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 获取服务配置
     * @param queryDO
     * @return
     */
    public List<Map> getServerInfos(EnvQueryDO queryDO) {
        // 查找对应命令, 先查找文件, 如果没有则查找数据库
        List<Map> serverInfos = this.getServerInfosFromFile();
        if (serverInfos == null) serverInfos = this.getServerInfos();

        List<Map> matchList = new ArrayList<>();
        serverInfos.forEach((data)->{
            if (queryDO.isMatch(data)) matchList.add(data);
        });

        return matchList;
    }

    /**
     * 从json文件中获取服务配置树
     * @return
     */
    public List<Map> getServerInfosFromFile() {
        String filePath = CommUtils.getDeployPath() + "/config/env.infos.json";
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            StringBuilder builder = new StringBuilder();
            CFileUtils.actionRead(filePath, ((lineNo, line) -> builder.append(line)), StandardCharsets.UTF_8.name());
            return JSONArray.parseArray(builder.toString(), Map.class);
        }
        return null;
    }

    /**
     * 获取服务配置
     * @return
     */
    public List<Map> getServerInfos() {
        // 查找对应命令
        String sql = "select s.*,u.*,s.descript sdescript, u.descript udescript from " +
                "ES_MACHINE_INFO u, ES_SERVER_INFO s where u.id = s.mid order by s.start_priority";
        return operator.queryForList(sql);
    }





    /**
     * 执行对应命令
     * @param actCode
     * @param serverInfos
     * @return 执行日志
     */
    public String doAction(String actCode, List<Map> serverInfos) {
        SshUserInfo userInfo = null;
        StringBuilder logBuilder = new StringBuilder();
        // TODO
        if ("mfree".equals(actCode) || "dfree".equals(actCode)) {
            Map<String, String> machineInfos = new HashMap<>();
            for (Map<String, Object> map : serverInfos) {
                String ip = (String) map.get("ip");
                String info = machineInfos.get(ip);
                String d = OBJUtils.isBlankStr(info)
                        ? machineInfos.put(ip, getServerDesc(map)) : machineInfos.put(ip, lineSeparator + getServerDesc(map));
            }

            return logBuilder.append(lineSeparator).toString();
        }

        for (Map<String, Object> map : serverInfos) {
            switch (actCode) {
                case "status" : logBuilder.append(executeStatus(map)); break;
                case "start" : logBuilder.append(executeStart(map)); break;
                case "restart" : logBuilder.append(executeRestart(map)); break;
                case "stop" : logBuilder.append(executeStop(map)); break;
                default : logBuilder.append("actCode=").append(actCode)
                        .append("不正确! ").append(getServerDesc(map)); break;
            }
        }
        return logBuilder.append(lineSeparator).toString();
    }

    /**
     * 监测服务 只检测端口是否可访问
     * @param serverMap
     * @return
     */
    private String executeStatus(Map<String, Object> serverMap) {
        String host = (String) serverMap.get("ip");
        String port = (String) serverMap.get("server_port");
        String server_type = (String) serverMap.get("server_type");
        String server_name = (String) serverMap.get("server_name");
        int serverPort = OBJUtils.isBlankStr(port) ? -1 : Integer.parseInt(port);
        boolean isAlive = "weblogic".equalsIgnoreCase(server_type) && !"flow".equals(server_name)
                ? CommUtils.weblogicIsAlive(host, serverPort) : CommUtils.serverIsAlive(host, serverPort);
        String log = ">> " + getServerDesc(serverMap) + tab + tab + (isAlive ? "运行中" : "------已停止");
        System.out.println(log);
        return log + lineSeparator;
    }

    /**
     * 启动服务
     * @param serverMap
     * @return
     */
    private String executeStart(Map<String, Object> serverMap) {
        SshUserInfo userInfo = getUserInfo(serverMap);
        String port = (String) serverMap.get("server_port");
        int serverPort = OBJUtils.isBlankStr(port) ? -1 : Integer.parseInt(port);
        String serverInfo = getServerDesc(serverMap);
        String log = null;
        if (CommUtils.serverIsAlive(userInfo.getHost(), serverPort)) {
            serverInfo = ">> "+serverInfo+" : 已是启动状态!!!";
            System.out.println(serverInfo);
            log = serverInfo + lineSeparator;
        } else {
            serverInfo = ">> 正在启动 : " + serverInfo;
            System.out.println(serverInfo);
            String startCmd = (String) serverMap.get("start_cmd");
            log = serverInfo + lineSeparator + executeStartCmd(userInfo, startCmd);
        }
        return log;
    }

    /**
     * 停止服务
     * @param serverMap
     * @return
     */
    private String executeStop(Map<String, Object> serverMap) {
        SshUserInfo userInfo = getUserInfo(serverMap);
        String stopCmd = (String) serverMap.get("stop_cmd");
        String log = ">> 正在停止 : " + getServerDesc(serverMap);
        System.out.println(log);
        return log + lineSeparator + executeCmd(userInfo, stopCmd);
    }

    /**
     * 重启服务
     * @param serverMap
     * @return
     */
    private String executeRestart(Map<String, Object> serverMap) {
        SshUserInfo userInfo = getUserInfo(serverMap);
        String startCmd = (String) serverMap.get("start_cmd");
        String stopCmd = (String) serverMap.get("stop_cmd");
        String restartCmd = (String) serverMap.get("restart_cmd");
        String log = ">> 正在重启 : " + getServerDesc(serverMap);
        System.out.println(log);
        if (OBJUtils.isNotBlankStr(restartCmd)) {
            log += executeStartCmd(userInfo, restartCmd);
        } else if (OBJUtils.notHasBlankStr(startCmd, stopCmd)){
            log += executeCmd(userInfo, stopCmd)
                   + executeStartCmd(userInfo, startCmd);
        }
        return log;
    }

    /**
     * 返回磁盘空间情况
     * @param serverMap
     * @return
     */
    private String diskFree(Map<String, Object> serverMap) {
        String log = ">> " + getServerDesc(serverMap) + " 硬盘使用情况:" + lineSeparator;
        SshUserInfo userInfo = getUserInfo(serverMap);
        log += executeCmd(userInfo, "df -h");
        System.out.println(log);
        return log;
    }


    /**
     * 返回内存空间情况
     * @param serverMap
     * @return
     */
    private String memoryFree(Map<String, Object> serverMap) {
        String log = ">> " + getServerDesc(serverMap) + " 内存使用情况:" + lineSeparator;
        SshUserInfo userInfo = getUserInfo(serverMap);
        log += executeCmd(userInfo, "free -m");
        System.out.println(log);
        return log;
    }


    /**
     * 执行启动命令
     * (不要执行耗时过长的任务, 因为不可靠)
     * @param userInfo ssh 用户信息
     * @param cmd 命令
     * @return 执行日志
     */
    public String executeStartCmd(SshUserInfo userInfo, String cmd) {
        ChannelShell channel = null;
        StringBuilder logBuilder = new StringBuilder();
        try {
            Session session = SshSessionContainer.getSession(userInfo);
            channel = (ChannelShell) session.openChannel("shell");
            channel.connect(9000);
            InputStream inputStream = channel.getInputStream();
            OutputStream outputStream = channel.getOutputStream();

            // 切换用户
            String data = "su - " + userInfo.getUsername();
            logBuilder.append(sendData(data, outputStream, inputStream, (line, count, inStream) -> {
                return line.contains("口令") || line.contains("su:")
                    || line.equalsIgnoreCase("Password:");
            }));

            // 输入密码
            logBuilder.append(sendData(userInfo.getPasswd(), outputStream, inputStream, (line, count, inStream) -> {
                if (line.contains("incorrect password") || line.contains("密码不正确")) {
                    throw new RuntimeException(line);
                }
                return count > 0;
            }));

            // 执行命令
            logBuilder.append(sendData(cmd, outputStream, inputStream, (line, count, inStream) -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                    return count > 0 && inputStream.available() == 0;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));

        } catch (Exception e) {
            logBuilder.append(">> 执行异常:").append(e.getMessage()).append(userInfo).append(lineSeparator);
            logBuilder.append(">> 执行目标:").append(cmd);
        } finally {
            if (channel != null) channel.disconnect();
            SshSessionContainer.removeSession(userInfo);
        }
        return logBuilder.toString();
    }

    /**
     * 发送数据到终端
     * @param data
     * @param outStream
     * @param inStream
     * @param breakHandler
     * @return
     */
    private String sendData(String data, OutputStream outStream,
                    InputStream inStream, BreakHandler breakHandler) {
        try {
            String line;
            int len = 0;
            boolean finish = false;
            byte[] buf = new byte[1024];
            StringBuilder logBuilder = new StringBuilder();
            // 发送数据
            outStream.write((data + lineSeparator).getBytes());
            outStream.flush();
            TimeUnit.MILLISECONDS.sleep(50);
            for (int count = 0; ; ) {
                while (inStream.available() > 0) {
                    line = new String(buf, 0, inStream.read(buf));
                    logBuilder.append(line);
                    if (OBJUtils.isNotBlankStr(line)) count++;
                    finish = breakHandler.isFinish(line, count, inStream);
                }
                if (finish) break;
                TimeUnit.MILLISECONDS.sleep(50);
            }
            return logBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    interface BreakHandler {
        /**
         * 根据响应判断命令是否完成了
         */
        boolean isFinish(String line, int count, InputStream inStream);
    }

    /**
     * 执行命令
     * @param userInfo ssh 用户信息
     * @param cmd 命令
     * @return 执行日志
     */
    public String executeCmd(SshUserInfo userInfo, String cmd) {
        if (!CommUtils.serverIsAlive(userInfo.getHost(), userInfo.getPort())) {
            return ">> 该机器无法访问:" + userInfo.toString();
        }
        ShellInstance shellInstance = new ShellInstance(userInfo);
        String executeLog = cmd + lineSeparator;
        try {
            executeLog += shellInstance.execute(cmd);
        } catch (Exception e) {
            executeLog = ">> 执行异常:" + userInfo + lineSeparator;
            executeLog += ">> 执行目标:" + cmd;
        }
        return executeLog;
    }


    /**
     * 返回终端用户信息
     * @param map
     * @return
     */
    private SshUserInfo getUserInfo(Map<String, Object> map) {
        String host = (String) map.get("ip");
        String port = (String) map.get("port");
        String userName = (String) map.get("username");
        String psword = (String) map.get("psword");
        int p = OBJUtils.isBlankStr(port) ? -1 : Integer.parseInt(port);
        return new SshUserInfo(host, p, userName, psword);
    }

    /**
     * 获取描述
     * @param map
     * @return
     */
    private String getServerDesc(Map<String, Object> map) {
        return map.get("env_code") + "-" + map.get("ip")+ ":" + map.get("server_port") + " - " +
                map.get("sdescript") + " " + map.get("server_name") + "-" + map.get("sversion");
    }

}
