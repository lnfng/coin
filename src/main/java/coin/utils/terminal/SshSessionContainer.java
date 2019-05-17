package coin.utils.terminal;

import java.util.Map;
import java.util.WeakHashMap;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * 会话缓存
 * @author Qian
 */
public class SshSessionContainer {
	
	/**
	 * 50秒 连接超时
	 */
	private final static int SESSION_TIMEOUT = 50*1000;
	
	private SshSessionContainer () {}

	/**
	 *  缓存作用
	 */
	private final static Map<UserInfo,Session> SESSION_CONTAINER = new WeakHashMap();
	
	/**
	 * 获取终端会话
	 * @param userInfo
	 * @return
	 */
	public static Session getSession(UserInfo userInfo) {
		Session session = SESSION_CONTAINER.get(userInfo);
		if (session != null && session.isConnected()) {
			return session;
		}
		session = createSession(userInfo);
		SESSION_CONTAINER.remove(userInfo);
		SESSION_CONTAINER.put(userInfo, session);
		return session;
	}
	
	/**
	 * 建立终端会话
	 * @param userInfo
	 * @return
	 */
	private static synchronized Session createSession(UserInfo userInfo) {
		Session sshSession;
		try {
			sshSession = new JSch().getSession(
				userInfo.getUsername(),
				userInfo.getHost(),
				userInfo.getPort());
			sshSession.setConfig("StrictHostKeyChecking", "no");
			sshSession.setPassword(userInfo.getPasswd());
			sshSession.connect(SESSION_TIMEOUT);
		} catch (JSchException e) {
			throw new RuntimeException("Create session "+userInfo+" exception!",e);
		}
		return sshSession;
	}
	
	
}
