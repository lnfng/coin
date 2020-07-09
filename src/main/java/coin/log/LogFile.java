package coin.log;

import coin.utils.CFileUtils;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 将日志记录到文件
 * @author Qian
 * @date 2019/12/22
 */
public class LogFile {

    // 默认文件名称
    private static final String DEF_LOG_FILE_NAME = "./Coin-Log-File.log";
    // 换行符
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    // 日期格式
    public SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String logPrefix;
    private String logPath;

    private LogFile(Class<?> holdClass) {
        holdClass = holdClass == null ? LogFile.class : holdClass;
        logPrefix = holdClass.getSimpleName();
        // 可设置 -DCoinLogPath=/home/weblogic/logs/Coin-Log-File.log
        logPath = System.getProperty("CoinLogPath",DEF_LOG_FILE_NAME);
    }

    /**
     * 获取实例
     * @param holdClass
     * @return
     */
    public static LogFile getInstance(Class<?> holdClass) {
        return new LogFile(holdClass);
    }

    /**
     *
     * @param log
     */
    public void log2File(String log) {
        log = "[ " + dateFormat.format(new Date()) + " " + logPrefix + " ] "
                + LINE_SEPARATOR + log + LINE_SEPARATOR;
        CFileUtils.appendToFile(log.getBytes(), logPath);
    }

}
