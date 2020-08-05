package coin.func;

import coin.annotation.Func;
import coin.func.base.BaseFunc;
import coin.func.base.FuncResult;
import coin.utils.*;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author Qian
 */
@Func(value="sftp", desc="测试")
public class SFtpFunc extends BaseFunc {


    @Override
    protected boolean isIllegalArgs(String[] args) {
        return false;
    }

    /**
        参数参考:
         {
             "host": "127.0.0.1",
             "port": 10022,
             "username": "weblogic",
             "password": "NjvdEoylCM",
             "localFile": "F:/tmp/db.conn.info.properties",
             "remotePath": "/weblogic/manau"
         }
     */
    @Override
    protected FuncResult action(String[] args) {

        System.out.println(">> 上传开始!");

        if (args.length < 1) {
            System.out.println(">> sftp 参数不能为空");
            return null;
        }

        String config = args[1];
        String localFile = CJSONUtils.findValue(config, "localFile");
        String host = CJSONUtils.findValue(config, "host");
        String portStr = CJSONUtils.findValue(config, "port");
        String username = CJSONUtils.findValue(config, "username");
        String password = CJSONUtils.findValue(config, "password");
        String remotePath = CJSONUtils.findValue(config, "remotePath");


        System.out.println(">> host : " + host);
        System.out.println(">> port : " + portStr);
        System.out.println(">> username : " + username);
        System.out.println(">> password : " + password);
        System.out.println(">> localFile : " + localFile);
        System.out.println(">> remotePath : " + remotePath);

        if (OBJUtils.isBlankStr(localFile)) {
            System.out.println(">> 参数localFile不能为空");
            return null;
        }

        try {
            File file = new File(localFile);
            if (!file.exists()) {
                System.out.println(">> 未找到文件:"+localFile);
                return null;
            }

            SSH2Client ssh2Client = new SSH2Client(
                    host, Integer.parseInt(portStr), username, password);

            ssh2Client.uploadFile(
                    remotePath, file.getName(), new FileInputStream(file));

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(">> 上传结束.");

        return null;
    }

}
