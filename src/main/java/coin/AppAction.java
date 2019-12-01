package coin;

import ch.ethz.ssh2.Connection;
import coin.annotation.Func;
import coin.func.BaseFunc;
import coin.utils.OBJUtils;
import coin.utils.terminal.ShellInstance;
import coin.utils.terminal.SshSessionContainer;
import coin.utils.terminal.UserInfo;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 入口类
 * @author Qian
 */
public class AppAction {
	
	private static final Map<String,Class<BaseFunc>> FUNC_MAP = new HashMap<>();
	private static final int BASE_BUF = 1024;

	static {
		//init(); // 初始化
	}


	public static void main(String[] args) throws Exception {

		UserInfo userInfo = new UserInfo();
		userInfo.setHost("192.168.43.124");
		userInfo.setPort(22);
		userInfo.setUsername("weblogic");
		userInfo.setPasswd("qj9090");

		//testChannelShell(userInfo);

		//testListCmd(userInfo);

		ArrayList<String> dataList = new ArrayList<>();
		dataList.add("su - root");
		dataList.add("qj9090");
		dataList.add("whoami");

		ShellInstance shellInstance = new ShellInstance(userInfo);
		String result = shellInstance.execute(dataList);
		System.out.println(">> result:" + result);


	}


	/**
	 * 测试shell的执行
	 * @param userInfo
	 * @throws Exception
	 */
	static void testChannelShell(UserInfo userInfo)  throws Exception {
		Session session = SshSessionContainer.getSession(userInfo);
		ChannelShell channel = (ChannelShell)session.openChannel("shell");
		channel.connect();
		InputStream inputStream = channel.getInputStream();
		OutputStream outputStream = channel.getOutputStream();

		PrintWriter printWriter = new PrintWriter(outputStream);

		String cmd = "su - root";
		printWriter.println(cmd);
		printWriter.flush();
		// 要加个时间差, 不然执行不成功
		TimeUnit.MILLISECONDS.sleep(1500);

		String cmd2 = "qj9090";
		printWriter.println(cmd2);

		String cmd3 = "whoami";
		printWriter.println(cmd3);
		printWriter.flush();
		// 非阻塞式读取
		for(byte[] buf=new byte[BASE_BUF];inputStream.available()>0;){
			int len=inputStream.read(buf);
			System.out.println(new String(buf, 0, len));
			TimeUnit.MILLISECONDS.sleep(500);
		}
		System.out.println("finish");
		channel.disconnect();
	}


	static void testListCmd(UserInfo userInfo) throws Exception {
		Session session = SshSessionContainer.getSession(userInfo);
		ChannelShell channel = (ChannelShell)session.openChannel("shell");
		channel.connect();
		InputStream inputStream = channel.getInputStream();
		OutputStream outputStream = channel.getOutputStream();


		ArrayList<String> dataList = new ArrayList<>();
		dataList.add("su - root");
		dataList.add("qj9090");
		dataList.add("whoami");

		for (String data : dataList) {
			// 向终端发送数据
			outputStream.write((data + "\n").getBytes());
			outputStream.flush();
			TimeUnit.MILLISECONDS.sleep(500);
		}


		// 非阻塞式读取
		FR:
		for (byte[] buf = new byte[BASE_BUF]; ;) {
			while (inputStream.available() > 0) {
				int len = inputStream.read(buf);
				System.out.println(new String(buf, 0, len));
				TimeUnit.MILLISECONDS.sleep(300);
				if (inputStream.available() <= 0) {
					break FR;
				}
			}
		}

		System.out.println("finish!!!");
		channel.disconnect();

	}





	/**
	 * 另一种客户端
	 * @param userInfo
	 * @throws Exception
	 */
	static void otherClient(UserInfo userInfo) throws Exception {
		// 连接
		Connection conn = new Connection(userInfo.getHost(), userInfo.getPort());
		conn.connect();

		// 认证
		boolean isAuthenticated = conn.authenticateWithPassword(userInfo.getUsername(), userInfo.getPasswd());
		if (!isAuthenticated){
			System.out.println("密码错误");
			throw new RuntimeException("Authentication failed.");
		}

		// 参考文档
		// https://www.cnblogs.com/qiangqiangqiang/p/7724015.html
		final ch.ethz.ssh2.Session session=conn.openSession();
		session.requestDumbPTY();  //建立虚拟终端
		session.startShell();      //打开一个shell
		PrintWriter printWriter = new PrintWriter(session.getStdin());
		String cmd = "su - root";
		printWriter.println(cmd);
		printWriter.flush();
		// 要加个时间差, 不然执行不成功
		TimeUnit.MILLISECONDS.sleep(1500);
		String cmd2 = "qj9090";
		printWriter.println(cmd2);
		String cmd3 = "whoami";
		printWriter.println(cmd3);
		printWriter.flush();

		InputStream inStream = session.getStdout();
		// 非阻塞式读取
		int exitStatus;
		StringBuilder result = new StringBuilder();

		BufferedReader in = new BufferedReader(new InputStreamReader(session.getStdout()));
		String msg = null;
		while((msg = in.readLine())!=null){
			System.out.println(msg);
		}
		System.out.println(session.getState());
		System.out.println(session.getExitStatus());
		System.out.println(session.getExitSignal());
		session.close();
		conn.close();
	}




	static void execute(String[] args) {
		// if(OBJUtils.isEmptyArray(args)){
		// 	System.out.println(">> no args setting, nothing to do!");
		// 	return ;
		// }

		// 取第一个不为空的参数
		String funcode = null;
		for (String arg : args) {
			if(OBJUtils.isNotBlankStr(arg))
				funcode = arg;
		}

		funcode = "sfile";
		BaseFunc func = getFunc(funcode);
		if (func == null) {
			System.out.println(">> 没有该功能编码, 请确认再操作. funcode="+funcode);
		} else {
			func.action(args);
		}
	}

	
	/**
	 * 获取功能类
	 * @param funcode
	 * @return
	 */
	private static BaseFunc getFunc(String funcode){
		Class<BaseFunc> fclass = FUNC_MAP.get(funcode);
		if (fclass != null){
			try {
				return fclass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return null;
	}
	
	
	/**
	 * 初始化
	 */
	private static void init(){
		try {
			String funcpath="coin.func"; // 指定的包路径
			String cpath = AppAction.class.getResource("/").getFile();
            String funcPath = cpath + funcpath.replace(".", "/");
            File cfile = new File(funcPath);
            for (File f : Objects.requireNonNull(cfile.listFiles())) {
            	String[] split = f.getName().split("\\.");
            	if (split.length != 2){
            		continue;
            	}
            	
            	String cname = funcpath+"."+split[0];
            	Class<?> tclass = Class.forName(cname);
            	if (tclass.isAnnotationPresent(Func.class)){
            		String funcCode = tclass.getAnnotation(Func.class).value();
            		FUNC_MAP.put(funcCode, (Class<BaseFunc>) tclass);
            	}
            }
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
