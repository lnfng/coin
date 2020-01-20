package coin.utils.terminal;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;
import java.io.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shell脚本执行实例 (不支持jdk1.6)
 * @author Qian
 */
public class ShellInstance {

	private SshUserInfo userInfo; // 会话信息
	private final static int BASE_BUF = 10240;
	private final static int CHANNEL_TIMEOUT = 9000;
	private String lineSeparator = System.getProperty("line.separator");

	public ShellInstance(SshUserInfo userInfo){
		this.userInfo = userInfo;
	}


	/**
	 * NOTE:不是很可靠
	 * 批量执行, 返回执行记录
	 * @param inputDataList 输入数据列表
	 * @return 执行记录
	 */
	public String execute(final List<String> inputDataList) {
		final StringBuilder result = new StringBuilder();
		ChannelShell channel = null;
		try {
			Session session = SshSessionContainer.getSession(userInfo);
			channel = (ChannelShell) session.openChannel("shell");
			channel.connect(CHANNEL_TIMEOUT);

			final AtomicInteger finishWrite = new AtomicInteger(0);
			final AtomicInteger finishRead = new AtomicInteger(0);
			final Semaphore semaphore = new Semaphore(0);
			final ChannelShell finalChannel = channel;

			new Thread(new Runnable() {
				public void run() {
					try {
						int size = inputDataList.size();
						PrintWriter writer = new PrintWriter(finalChannel.getOutputStream());
						for (int i = 0; i < size; i++) {
							String data = inputDataList.get(i);
							// 向终端发送数据
							writer.println(data);
							writer.flush();
							TimeUnit.MILLISECONDS.sleep(100);
							if ((i + 1) == size) {
								// 数据已发送完毕
								finishWrite.set(1);
							}
							// 获取再次发送数据的信号
							semaphore.acquire();
						}

					} catch (Exception e) {
						// ignore
					}
				}
			}).start();

			new Thread(new Runnable() {
				public void run() {
					try {
						int len = 0;
						// 非阻塞式读取
						InputStream inputStream = finalChannel.getInputStream();
						for (byte[] buf = new byte[BASE_BUF];;) {
							while (inputStream.available() > 0) {
								len = inputStream.read(buf);
								result.append(new String(buf, 0, len));
								if (inputStream.available() <= 0) {
									semaphore.release();
									break;
								}
							}
							TimeUnit.MILLISECONDS.sleep(50);
							if (finishWrite.get() == 1 && inputStream.available() <= 0) {
								TimeUnit.MILLISECONDS.sleep(150);
								finishRead.set(1);
								break;
							}
						}

					} catch (Exception e) {
						// ignore
					}
				}
			}).start();


			for (;;) {
				TimeUnit.MILLISECONDS.sleep(50);
				if (finishRead.get() == 1) {
					channel.disconnect();
					SshSessionContainer.removeSession(userInfo);
					break;
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result.toString();
	}


	/**
	 * 执行shell命令
	 * @param shellCmd shell命令
	 * @return 执行结果
	 */
	public String execute(String shellCmd){
		ChannelExec exec = null;
		Session session = null;
		StringBuilder result = new StringBuilder();
		try {
			session = SshSessionContainer.getSession(userInfo);
			exec = (ChannelExec) session.openChannel("exec");
			exec.setCommand(shellCmd);
			exec.setInputStream(null);
			InputStream inStream=exec.getInputStream();
			exec.connect(CHANNEL_TIMEOUT);

			// 非阻塞式读取
		    for(byte[] buf=new byte[BASE_BUF];;){
		        while(inStream.available()>0){
		          int len=inStream.read(buf);
		          if(len<0){break;}
		          result.append(new String(buf, 0, len));
		        }
		        if(exec.isClosed()){
		          if(inStream.available()>0){continue;}
		          break;
		        }
		    }

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (exec != null) {
				exec.disconnect();
				SshSessionContainer.removeSession(userInfo);
			}
		}
		return result.toString();
	}


	public SshUserInfo getSessionInfo() {
		return userInfo;
	}
	
}
