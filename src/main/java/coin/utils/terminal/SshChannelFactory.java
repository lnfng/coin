package coin.utils.terminal;

import com.jcraft.jsch.*;

/**
 * 通道工厂类
 * @author Qian
 */
public class SshChannelFactory {
	
	private SshChannelFactory() {}
	
	
	public static <T> T getChannel(UserInfo userInfo, Class<T> clazz){
		try {
			Session session = SshSessionContainer.getSession(userInfo);
			return (T)session.openChannel(ChannelType.getChannelType(clazz));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 关闭会话, 谨慎操作, 可能引起该会话连接的通道异常
	 * @param userInfo
	 */
	public static void disConnSession(UserInfo userInfo){
		Session session = SshSessionContainer.getSession(userInfo);
		if (session != null) {
			session.disconnect();
		}
	}
	
	
	/**
	 * 通道类型
	 * @author Qian
	 */
	public enum ChannelType{
		shell,exec,sftp;
		
		public static String getChannelType(Class<?> clazz){
			ChannelType ctype = null;
			if(ChannelShell.class == clazz){
				ctype = ChannelType.shell;
			}else if(ChannelSftp.class == clazz){
				ctype = ChannelType.sftp;
			}else if(ChannelExec.class == clazz){
				ctype = ChannelType.exec;
			}else {
				throw new RuntimeException("Unknow channel type!!!");
			}
			return ctype.toString();
		}
	}

}
