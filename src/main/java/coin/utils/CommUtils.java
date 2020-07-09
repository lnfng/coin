package coin.utils;

import java.io.IOException;
import java.net.*;
import java.util.Objects;

/**
 * 公共工具类
 * @author Qian
 * @date 2019/12/10
 */
public class CommUtils {

    private CommUtils() {}

    /**
     * 检查服务是否可用
     * @param host 主机IP
     * @param port 端口
     * @return
     */
    public static boolean serverIsAlive(String host, int port) {
        if (OBJUtils.isBlankStr(host) || port < 0) {
            return false;
        }
        boolean isAlive = false;
        try {
            //连接服务器
            Socket connect = new Socket();
            connect.connect(new InetSocketAddress(host, port), 3700);
            isAlive = connect.isConnected();
            connect.close();
        } catch (IOException e) {
            // ignore
        }
        return isAlive;
    }


    /**
     * 检查服务是否可用
     * @param host 主机IP
     * @param port 端口
     * @return
     */
    public static boolean weblogicIsAlive(String host, int port) {
        if (OBJUtils.isBlankStr(host) || port < 0) {
            return false;
        }
        try {
            URL url = new URL("http://"+host+":"+port+"/console/login/LoginForm.jsp");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5 * 1000);
            connection.connect();
            connection.getOutputStream().write(886);
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            // ignore
        }
        return false;
    }


    /**
     * 获取部署路径
     * @return
     */
    public static String getDeployPath() {
        Class<CommUtils> aClass = CommUtils.class;
        Package aPackage = aClass.getPackage();
        String name = aClass.getSimpleName() + ".class";
        if (aPackage != null) {
            name = aPackage.getName().replace(".", "/") + "/" + name;
        }
        String path = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(name)).getPath();
        if (path.contains(".jar!")) {
            int index = path.substring(0, path.indexOf(".jar!")).lastIndexOf("/");
            path = path.substring("file:".length(), index+1);
        } else if(path.contains("/target/classes/")) {
            path = path.substring(0, path.indexOf("/target/classes/")) + "/target/";
        } else if(path.contains("/bin/")) {
            path = path.substring(0, path.lastIndexOf("bin/") + 1);
        }
        return path;
    }


}
