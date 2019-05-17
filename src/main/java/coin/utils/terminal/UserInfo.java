package coin.utils.terminal;

/**
 * 用户信息
 * @author Qian
 */
public class UserInfo {

	private String id;
	private String host;		// IP地址
	private Integer port;		// 端口
	private String username;	// 用户名
	private transient String passwd; // 密码
	
	
	public String getHost() {
		return host;
	}
	public Integer getPort() {
		return port;
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
		return (this.toString()+this.passwd)
				.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null || !(obj instanceof UserInfo)){
			return false;
		}
		UserInfo sobj=(UserInfo) obj;
        return this.toString().equals(sobj.toString())
                && this.passwd !=null 
                && this.passwd.equals(sobj.getPasswd());
    }
	
	@Override
	public String toString() {
		return "[host:"+this.host
				+" port:"+this.port
				+" username"+this.username+"]";
	}
	
	
	
}
