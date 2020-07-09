package coin.func;

import coin.annotation.Func;
import coin.func.base.BaseFunc;
import coin.func.base.FuncResult;
import coin.utils.CFileUtils;
import coin.utils.CommUtils;
import coin.utils.terminal.FtpUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Qian
 */
@Func(value="ftp", desc="测试")
public class FtpFunc extends BaseFunc {


    @Override
    protected boolean isIllegalArgs(String[] args) {
        return false;
    }

    @Override
    protected FuncResult action(String[] args) {

        System.out.println(">> 测试FTP!");

        String host = "132.120.115.22";
        String port = "21";
        String username = "sale";
        String password = "aiLk@Aug22";
        String remotePath = "/ngbss/sale/ordercenter/file_out";
        // 建立ftp
        FtpUtil ftpUtil = new FtpUtil(host, port, username, password, remotePath);


        // 获取ftp文件数据
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (!ftpUtil.downloadFile("seven_day_package_20200602.txt", outputStream)) {
            System.out.println(">> CreateGoodsFromSourceCenterTimer: ftp文件获取失败!");
        }
        // 解析文件内容
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        CFileUtils.actionRead(inputStream, new CFileUtils.ReadLineHandler() {
            public void handLineData(int lineNo, String line) {
                System.out.println(line);
            }
        });

        return null;
    }

}
