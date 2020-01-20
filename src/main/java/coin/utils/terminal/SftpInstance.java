package coin.utils.terminal;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Sftp 实例 (不支持jdk1.6)
 * @author Qian
 */
public class SftpInstance {
	
	private ChannelSftp sftp;	// sftp 通道
	private SshUserInfo userInfo; 	// 会话信息
	private Session session;    // 会话
	private final static int CHANNEL_TIMEOUT = 9000;
	
	
	public SftpInstance(SshUserInfo userInfo){
		this.userInfo = userInfo;
	}
	
	
	/**
	 * 上传文件
	 * @param fileName 上传文件名
	 * @param localPath 本地路径
	 * @param remotePath 远程路径
	 * @return
	 */
	public boolean upload(String fileName, String localPath, String remotePath) {
        try {
			return this.upload(
					new FileInputStream(new File(localPath+"/"+fileName)),
					fileName, 
					remotePath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 上传文件
	 * @param inStream 输入流
	 * @param fileName 文件名
	 * @param remotePath 远程路径
	 * @return
	 */
	public boolean upload(InputStream inStream, String fileName, String remotePath) {
		try {
			this.connection();
			sftp.cd(remotePath);
			sftp.put(inStream, fileName);
			return true;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally{
			this.disconnect();
			try {inStream.close();}catch (Exception ignored){}
		}
	}
	
	
	/**
	 * 下载文件
	 * @param fileName 下载的文件名
	 * @param savePath 下载保存路径
	 * @param directory 远程路径
	 */
    public void download(String fileName, String savePath, String directory) {
        try {
			this.download(
				new FileOutputStream(new File(savePath+"/"+fileName)),
				fileName, 
				directory);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
    }
	
	/**
	 * 下载文件
	 * @param outStream 输出流
	 * @param fileName  下载的文件名
	 * @param directory 远程路径
	 */
	public void download(OutputStream outStream, String fileName, String directory){
		try {
			this.connection();
			sftp.cd(directory);
			sftp.get(fileName, outStream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally{
			this.disconnect();
			try {outStream.close();}catch (Exception ignored){}
		}
	}
	
	/**
     * 获取远程路径下的文件
     * @param directory 远程路径
     */
	public List<LsEntry> getFileListEntry(String directory) {
    	List<LsEntry> ls;
    	try {
    		this.connection();
        	ls = sftp.ls(directory);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally{
			this.disconnect();
		}
    	if(ls==null){
    		ls = new ArrayList<LsEntry>(1);
    	}
        return ls;
    }
	
	/**
	 * 关闭当前通道
	 */
	private void disconnect() {
		if(sftp!=null && sftp.isConnected()){
			sftp.quit();
			sftp.disconnect();
			SshSessionContainer.removeSession(userInfo);
		}
    }

	/**
	 * 连接通道
	 * @throws JSchException
	 */
	private void connection() throws JSchException {
		session = SshSessionContainer.getSession(userInfo);
		sftp = (ChannelSftp) session.openChannel("sftp");
		sftp.connect(CHANNEL_TIMEOUT);
	}
	
	public SshUserInfo getSessionInfo() {
		return userInfo;
	}
	
}
