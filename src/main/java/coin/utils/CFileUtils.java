package coin.utils;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Qian
 * @date 2019/11/6
 */
public class CFileUtils {

    // 工具类私有化构造方法
    private CFileUtils() {}
    // 默认字符集
    public static final String DEF_CHARSET = "UTF-8";
    // 文件流缓存大小
    public final static int BASE_BUF = 1024 * 16;


    /**
     * 将数据写到文件
     * 注意: 如果文件存在则会先删除再写入
     * @param path 源文件路径
     * @param fileName 文件名称
     * @param bytes 文件字节数据
     */
    public static void writeToFile(String path, String fileName, byte[] bytes) {

        FileChannel outChannel = null;
        try {
            File file = new File(path + "/" + fileName);
            File dir = new File(file.getParent());
            boolean isCreated = dir.exists() ?
                    file.delete() && file.createNewFile() : dir.mkdirs() && file.createNewFile();
            outChannel = new RandomAccessFile(file, "rw").getChannel();
            outChannel.write(ByteBuffer.wrap(bytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (outChannel != null) outChannel.close() ;
            } catch (Exception e) {

            }
        }
    }


    /**
     * 将数据追加到文件末尾
     * @param bytes 文件字节数据
     * @param sourcePath 源文件路径
     */
    public static void appendToFile(byte[] bytes, String sourcePath) {

        FileChannel outChannel = null;
        try {
            File file = new File(sourcePath);
            File dir = new File(file.getParent());
            boolean isCreated = dir.exists() ? file.createNewFile() : dir.mkdirs() && file.createNewFile();
            RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
            outChannel = accessFile.getChannel();
            outChannel.position(accessFile.length());
            outChannel.write(ByteBuffer.wrap(bytes));
            accessFile.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (outChannel != null) outChannel.close();
            } catch (Exception e) {

            }
        }
    }


    /**
     * 复制文件
     * @param sourcePath 源文件路径
     * @param savePath 目标路径
     */
    public static void copyFileByNIO(String sourcePath, String savePath) {

        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            File dir = new File(savePath);
            File file = new File(sourcePath);
            boolean isCreated = !dir.exists() && dir.mkdirs();
            inChannel = new FileInputStream(file).getChannel();
            outChannel = new RandomAccessFile(savePath+file.getName(), "rw").getChannel();
            for (ByteBuffer buffer = ByteBuffer.allocateDirect(BASE_BUF);
                 inChannel.read(buffer) != -1; ) {
                // Prepare for writing
                buffer.flip();
                outChannel.write(buffer);
                // Prepare for reading
                buffer.clear();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (inChannel != null) inChannel.close();
                if (outChannel != null) outChannel.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }



    /**
     * 复制文件
     * @param sourcePath 源文件路径
     * @param savePath 目标路径
     */
    public static void copyFile(String sourcePath, String savePath) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try{
            File dir = new File(savePath);
            File file = new File(sourcePath);
            boolean isCreated = !dir.exists() && dir.mkdirs();
            fis = new FileInputStream(file);
            fos = new FileOutputStream(savePath+file.getName());
            byte[] buf=new byte[BASE_BUF];
            for (int len = 0; (len=fis.read(buf)) != -1; ) {
                fos.write(buf, 0, len);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (fos != null) try { fos.close(); } catch (Exception e) {}
            if (fis!=null) try {fis.close();} catch (Exception e) {}
        }
    }


    /**
     * 文件是否已存在
     * @param filePath 文件路径
     * @return 是否存在
     */
    public static boolean isExists(String filePath) {
        return filePath != null && new File(filePath).exists();
    }


    /**
     * 按行读取文本文件
     * @param inputStream 文件路径
     * @param handler 行处理器
     */
    public static void actionRead(InputStream inputStream, ReadLineHandler handler) {
        actionRead(inputStream, handler, DEF_CHARSET);
    }


    /**
     * 按行读取文本文件
     * @param filePath 文件路径
     * @param handler 行处理器
     */
    public static void actionRead(String filePath, ReadLineHandler handler) {
        actionRead(filePath, handler, DEF_CHARSET);
    }


    /**
     * 按行读取文本文件
     * @param filePath 文件路径
     * @param handler 行处理器
     * @param charset 字符集 缺省值:UTF-8
     */
    public static void actionRead(String filePath, ReadLineHandler handler, String charset) {
        try {
            actionRead(new FileInputStream(filePath), handler, charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 按行读取文本文件
     * @param inputStream 输入流
     * @param handler 行处理器
     * @param charset 字符集 缺省值:UTF-8
     */
    public static void actionRead(InputStream inputStream, ReadLineHandler handler, String charset) {
        if (handler == null || inputStream == null) {
            throw new IllegalArgumentException("数据流及行处理器不能为空!");
        }
        // 行数从1开始
        int lineNo = 1;
        BufferedReader reader = null;
        charset = charset == null ? DEF_CHARSET : charset;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, charset));
            for (String line=null; (line=reader.readLine()) != null; lineNo++) {
                // 按行处理
                handler.handLineData(lineNo, line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }


    /**
     * 行数据处理器
     */
    public interface ReadLineHandler {

        /**
         * 行数据处理
         * @param lineNo 行号
         * @param line 行数据
         */
        void handLineData(int lineNo, String line);

    }

}
