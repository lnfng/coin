package coin.utils;

import ch.ethz.ssh2.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Linux 终端操作类
 * @author Qian
 * @date 2020/4/5
 */
public class SSH2Client {

    private int port;
    private String host, username, password;
    // 默认端口
    private final static int def_port = 22;
    // 文件流缓存大小
    public final static int BASE_BUF = 1024 * 16;

    private Connection conn;


    /**
     * @param host 主机IP或名称
     * @param username 用户名
     * @param password 密码
     */
    public SSH2Client(String host, String username, String password) {
        this(host, def_port, username, password);
    }

    /**
     * @param host 主机IP或名称
     * @param port 主机端口
     * @param username 用户名
     * @param password 密码
     */
    public SSH2Client(String host, int port, String username, String password) {
        if (port < 0 || port > 65535 || OBJUtils.hasBlankStr(host, username, password)) {
            throw new IllegalArgumentException("参数不正确, 请检查.");
        }
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }


    /**
     * 下载文件
     * @param remotePath 远程文件路径
     * @param fileName 目标文件名称
     * @param outputStream 输出流
     * @param encoding 字符集:GBK,UTF-8...
     * @return 是否下载成功
     */
    public void downloadFile(String remotePath, String fileName, OutputStream outputStream) {
        this.downloadFile(remotePath, fileName, outputStream, null);
    }


    /**
     * 下载文件
     * @param remotePath 远程文件路径
     * @param fileName 目标文件名称
     * @param outputStream 输出流
     * @param encoding 字符集:GBK,UTF-8...
     */
    public void downloadFile(String remotePath, String fileName, OutputStream outputStream, String encoding) {
        try {
            initConn();
            SCPClient scpClient = conn.createSCPClient();
            if (encoding != null) scpClient.setCharset(encoding);
            //从远程获取文件
            String remoteFile = remotePath + "/" + fileName;
            SCPInputStream inputStream = scpClient.get(remoteFile);
            byte[] buf=new byte[BASE_BUF];
            for (int len = 0; (len=inputStream.read(buf)) != -1;) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            disconnection();
        }
    }


    /**
     * 上传文件
     * @param remotePath 远程文件路径
     * @param fileName 文件名称
     * @param inputStream 源文件流
     */
    public void uploadFile(String remotePath, String fileName, InputStream inputStream) {
        try {
            initConn();
            // 文件全路径
            fileName = remotePath + "/" + fileName;
            SFTPv3Client client = new SFTPv3Client(conn);
            // 新建文件
            SFTPv3FileHandle fileHandle = client.createFileTruncate(fileName);
            long fileOffset = 0;
            byte[] buf=new byte[BASE_BUF];
            for (int len = 0; (len=inputStream.read(buf)) != -1; fileOffset += len) {
                client.write(fileHandle, fileOffset, buf, 0, len);
            }
            // 必须关掉, 避免数据缺失
            client.closeFile(fileHandle);
            client.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // ignore
            }
            disconnection();
        }
    }



    /**
     * SFTPv3DirectoryEntry 包含文件名字及其他属性
     * @param remotePath 远程路径
     * @return
     */
    public List<SFTPv3DirectoryEntry> listFiles(String remotePath) {
        try {
            initConn();
            SFTPv3Client client = new SFTPv3Client(conn);
            return client.ls(remotePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            disconnection();
        }
    }



    private void initConn() throws Exception {
        conn = new Connection(host, port);
        conn.connect();
        if (!conn.authenticateWithPassword(username, password)) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
    }

    private void disconnection() {
        if (conn != null) conn.close();
    }


}
