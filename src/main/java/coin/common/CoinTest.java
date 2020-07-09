package coin.common;

import coin.annotation.Func;
import coin.func.base.BaseFunc;
import coin.log.LogFile;
import coin.utils.*;
import org.reflections.Reflections;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * @author Qian
 * @date 2019/12/17
 */
public class CoinTest {

    private static final LogFile log = LogFile.getInstance(CoinTest.class);

    public static void main(String[] args) throws Exception {

        DBOperator dbOperator = DBOperator.getDBOperator(DBOperator.Env.TEST);




        //String deployPath = CommUtils.getDeployPath();
        //System.out.println(deployPath);
        //
        //EnvQueryDO queryDO = new EnvQueryDO();
        //queryDO.setEnvCode("T");
        //queryDO.setServerNames("app");
        //queryDO.setActCode("status");
        //queryDO.setVersion("v1");
        //
        //List<Map> infos = new EnvManager().getServerInfos(queryDO);
        //System.out.println(infos.size());


    }




    static void ssh2Test() {
        /*String hostname = "47.115.86.148";
        int port = 22;
        String username = "root";
        String password = "qj9090";
        //指明连接主机的IP地址
        Connection conn = new Connection(hostname,port);
        Session ssh = null;
        try  {
            //连接到主机
            conn.connect();
            //使用用户名和密码校验
            boolean isconn = conn.authenticateWithPassword(username, password);
            if (!isconn)  {
                System.out.println("用户名称或者是密码不正确");
            } else {
                System.out.println("已经连接OK");
                //以下是linux的示例
                //将本地conf/server_start.sh传输到远程主机的/opt/pg944/目录下
                SCPClient scpClient = conn.createSCPClient();
                SCPInputStream inputStream = scpClient.get("/root/commands/zookeeper//zk-start.sh");//从远程获取文件
                CFileUtils.actionRead(inputStream, new CFileUtils.ReadLineHandler() {
                    @Override
                    public void handLineData(int lineNo, String line) {
                        System.out.println(line);
                    }
                });

                SFTPv3Client client = new SFTPv3Client(conn);
                //client.setCharset("GBK");
                List<SFTPv3DirectoryEntry> files = client.ls("/root/");
                for (SFTPv3DirectoryEntry entry : files) {
                    if (".".equals(entry.filename) || "..".equals(entry.filename)) {
                        continue;
                    }
                    System.out.println(entry.filename);
                }



                //执行命令
                //ssh = conn.openSession();
                //ssh.execCommand("ps -ef|grep zoo");
                //ssh.execCommand("perl /root/hello.pl");
                //只允许使用一行命令，即ssh对象只能使用一次execCommand这个方法，多次使用则会出现异常.
                //使用多个命令用分号隔开
                //ssh.execCommand("cd /root; sh hello.sh");

                *//* 执行windows系统命令的示例
                Session sess = conn.openSession();
                sess.execCommand("ipconfig");
                *//*

                //将Terminal屏幕上的文字全部打印出来
                *//*InputStream is = new StreamGobbler(ssh.getStdout());
                BufferedReader brs = new BufferedReader(new InputStreamReader(is));
                while (true)  {
                    String line = brs.readLine();
                    if (line == null) {
                        break;
                    }
                    System.out.println(line);
                }*//*
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            //连接的Session和Connection对象都需要关闭
            if(ssh!=null) {
                ssh.close();
            }
            conn.close();
        }*/

    }



    /**
     * 测试加载的时间
     * @throws Exception
     */
    static void test() throws Exception {

        // jar 包的读, 不能写
        // 路径拼接
        URL url = new URL("jar:file:/C:/Users/Jon/Test/foo.jar!/com/whatever/Foo.class");
        // 标准输入流
        InputStream in = url.openStream();

        // 指定包路径的前缀
        long start = System.currentTimeMillis();
        String funcPathPrefix="coin.func";
        Reflections reflections = new Reflections(funcPathPrefix);
        // 获取BaseFunc所有子类
        Set<Class<? extends BaseFunc>> subTypes = reflections.getSubTypesOf(BaseFunc.class);
        for (Class<? extends BaseFunc> c : subTypes) {
            if (c.isAnnotationPresent(Func.class)) {
                String[] funcCode = c.getAnnotation(Func.class).value();
                System.out.println(funcCode);
            }
        }
        System.out.println(">> reflections : " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        String cfgPath = "coin/resources/func-class-list.txt";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = loader.getResourceAsStream(cfgPath);
        CFileUtils.actionRead(inputStream, new CFileUtils.ReadLineHandler() {
            public void handLineData(int lineNo, String line) {
                try {
                    Class<?> aClass = Class.forName(line);
                    if (aClass.isAnnotationPresent(Func.class)) {
                        String[] funcCode = aClass.getAnnotation(Func.class).value();
                        System.out.println(">> read : " + funcCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println(">> read : " + (System.currentTimeMillis() - start));


        Class<?> aClass = Class.forName("coin.func.base.BaseFunc");
        System.out.println(aClass);
    }

}
