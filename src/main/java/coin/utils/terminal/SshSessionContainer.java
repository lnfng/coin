package coin.utils.terminal;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 会话缓存 (不支持jdk1.6)
 * @author Qian
 */
public class SshSessionContainer {
	
	/**
	 * 50秒 连接超时
	 */
	private final static int SESSION_TIMEOUT = 50*1000;

	/**
	 *  缓存作用
	 */
	private final static Map<SshUserInfo, Session> SESSION_CONTAINER = new ConcurrentHashMap();

	/**
	 * 持有session的个数
	 */
	private final static Map<SshUserInfo, AtomicInteger> HOLDER = new ConcurrentHashMap();

	/**
	 * 持有锁
	 */
	private final static Lock lock = new ReentrantLock();


	private SshSessionContainer () {}

	/**
	 * 获取终端会话
	 * @param userInfo
	 * @return
	 */
	public static Session getSession(SshUserInfo userInfo) {
		if (userInfo == null) return null;
		lock.lock();
		try {
			Session session = SESSION_CONTAINER.get(userInfo);
			AtomicInteger atomicInteger = HOLDER.get(userInfo);
			if (session == null || !session.isConnected()) {
				session = createSession(userInfo);
				SESSION_CONTAINER.put(userInfo, session);
			}
			if (atomicInteger == null) {
				HOLDER.put(userInfo, new AtomicInteger(1));
			} else {
				atomicInteger.incrementAndGet();
			}
			return session;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 从容器中移除, 并关闭
	 * @param userInfo
	 * @return
	 */
	public static void removeSession(SshUserInfo userInfo) {
		if (SESSION_CONTAINER.containsKey(userInfo)) {
			lock.lock();
			try {
				AtomicInteger atomicInteger = HOLDER.get(userInfo);
				if (atomicInteger.decrementAndGet() <= 0) {
					SESSION_CONTAINER.remove(userInfo).disconnect();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				lock.unlock();
			}
		}
	}
	
	/**
	 * 建立终端会话
	 * @param userInfo
	 * @return
	 */
	private static Session createSession(SshUserInfo userInfo) {
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
