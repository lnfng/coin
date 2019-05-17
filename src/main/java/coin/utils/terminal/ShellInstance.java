package coin.utils.terminal;

import java.io.*;
import com.jcraft.jsch.*;
import java.util.concurrent.TimeUnit;

/**
 * Shell脚本执行实例。
 * @author Qian
 */
public class ShellInstance {
	
	private UserInfo userInfo; // 会话信息
	private final static int BASE_BUF = 1024;
	private final static int CHANNEL_TIMEOUT = 8000;
	
	public ShellInstance(UserInfo userInfo){
		this.userInfo = userInfo;
	}

	/**
	 * 执行shell命令
	 * @param shellCmd 命令串
	 * @return 执行结果
	 */
	public ExecResult execute(String shellCmd){
		ExecResult rs = new ExecResult();
		ChannelExec exec = null;
		try {
			exec = SshChannelFactory
				.getChannel(userInfo, ChannelExec.class);
			exec.setCommand(shellCmd);
			exec.setInputStream(null);
			InputStream inStream=exec.getInputStream();
			exec.connect(CHANNEL_TIMEOUT);
			
			// 非阻塞式读取
			int exitStatus = ExecResult.DEF_CODE;
			StringBuilder result = new StringBuilder(); 
		    for(byte[] buf=new byte[BASE_BUF];;){
		        while(inStream.available()>0){
		          int len=inStream.read(buf);
		          if(len<0){break;}
		          result.append(new String(buf, 0, len));
		        }
		        if(exec.isClosed()){
		          if(inStream.available()>0){continue;}
		          exitStatus = exec.getExitStatus();
		          break;
		        }
		        try{TimeUnit.MILLISECONDS.sleep(100);}catch(Exception ee){}
		    }
		    rs.setExitStatus(exitStatus);
		    rs.setResult(result.toString());
			
		} catch (Exception e) {
			rs.setExitStatus(ExecResult.EXCEPT_CODE);
		    rs.setResult(e.getMessage());
			e.printStackTrace();
		} finally {
			if(exec!=null)exec.disconnect();
		}
		return rs; 
	}



    /**
     * 执行结果
     *
     * 状态码	描述
     *	0		命令成功结束
     *	1		通用未知错误　　
     *	2		误用Shell命令
     *	126		命令不可执行
     *	127		没找到命令
     *	128		无效退出参数
     *	128+x	Linux信号x的严重错误
     * 	130		命令通过Ctrl+C控制码越界
     *  255(-1)	退出码越界
     * @author Qian
     */
	public static class ExecResult{
		public final static int DEF_CODE = -100;
		public final static int EXCEPT_CODE = -999;
		
		private int exitStatus = DEF_CODE; // 命令退出状态
		private String result;			   // 命令执行结果
		
		public void setExitStatus(int exitStatus) {
			this.exitStatus = exitStatus;
		}
		public int getExitStatus() {
			return exitStatus;
		}
		public void setResult(String result) {
			this.result = result;
		}
		public String getResult() {
			return result;
		}
		
		@Override
		public String toString() {
			return "exitStatus : " +exitStatus+ " result : \n"+result;
		}
	}
	
	public UserInfo getSessionInfo() {
		return userInfo;
	}
	
}
