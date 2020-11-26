package coin.utils.terminal;

/**
 * 用户信息
 * @author Qian
 */
public class SshUserInfo {

	private String id;
	private String host;		// IP地址
	private Integer port;		// 端口
	private String username;	// 用户名
	private String passwd; // 密码
	private String execUser; // 执行用户
	private String envCode; // 所属环境编码
	private static int DEF_PORT = 22; // 默认端口

	public SshUserInfo() {

	}

	public SshUserInfo(String host, String username, String passwd) {
		this(host, DEF_PORT, username, passwd);
	}

	public SshUserInfo(String host, int port, String username, String passwd) {
		this.host = host;
		this.username = username;
		this.passwd = passwd;
		this.port = port;
	}

	public String getHost() {
		return host;
	}
	public Integer getPort() {
		return port == null ? DEF_PORT : port;
	}
	public String getUsername() {
		return username;
	}
	public String getPasswd() {
		return passwd;
	}
    public String getId() {
        return id;
    }
	public String getExecUser() {
		return execUser;
	}
	public String getEnvCode() {
		return envCode;
	}

	public void setEnvCode(String envCode) {
		this.envCode = envCode;
	}
	public void setExecUser(String execUser) {
		this.execUser = execUser;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public void setId(String id){this.id = id;}
	
	@Override
	public int hashCode() {
		return (this.toString()+this.passwd).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof SshUserInfo)){
			return false;
		}
		SshUserInfo userInfo=(SshUserInfo) obj;
        return this.toString().equals(userInfo.toString());
    }
	
	@Override
	public String toString() {
		return "[host:" + this.host +
				" port:" + this.port +
				" username:" + this.username +
				" passwd:" + this.passwd +
				" envCode:" + this.envCode +
				" execUser:" + this.execUser + "]";
	}
	
	
	
}
