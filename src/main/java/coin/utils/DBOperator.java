package coin.utils;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;


/**
 * 测试用途
 * 数据库操作对象
 * @author Qian
 */
public class DBOperator {

	private Connection con;	// 创建数据库连接对象
	private DBUserInfo DBUserInfo;		// 连接账号信息

	public DBOperator(){}
	public DBOperator(DBUserInfo DBUserInfo){this.DBUserInfo = DBUserInfo;}


	public static void main(String[] args) {
		DBOperator dbop = DBOperator.getDBOperator(Env.TEST);

		List list = dbop.queryForList("select * from wssmall_ecs.es_regions_zb t where t.add_level = ?", "1");
		System.out.println(">> list:"+list);

	}

	/**
	 * 获取对应环境连接,其他的可以通过构造函数创建
	 * @param env TEST/PRODUCT_R/PRODUCT_R_W
	 * @return
	 */
	public static DBOperator getDBOperator(Env env){
		if(env==null)throw new RuntimeException("env is required!!!");
		return new DBOperator(env.getUserInfo());
	}


	/**
	 * 环境变量 TEST/PRODUCT_R/PRODUCT_R_W</br>
	 * TEST 		测试环境用户</br>
	 * PRODUCT_R	生产只读用户</br>
	 * PRODUCT_R_W	生产管理用户</br>
	 * @author Qian
	 */
	public enum Env {
		TEST, PRODUCT_R, PRODUCT_R_W;

		public DBUserInfo getUserInfo(){
			try {
				InputStream inputStream;
				File cfgFile = new File(CommUtils.getDeployPath() + "/config/db.conn.info.properties");
				if (cfgFile.exists()) {
					inputStream = new FileInputStream(cfgFile);
				} else {
					ClassLoader loader = Thread.currentThread().getContextClassLoader();
					inputStream = loader.getResourceAsStream("coin/resources/db.conn.info.properties");
				}
				Properties userInfos = new Properties();
				userInfos.load(inputStream);
				return new DBUserInfo(
						userInfos.getProperty(this+".USER"),
						userInfos.getProperty(this+".PASSWD"),
						userInfos.getProperty(this+".URL"),
						userInfos.getProperty(this+".DRIVER")
				);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}


	/**
	 * @param sql 数据库执行(增、删、改)语句
	 * @param params
	 * @return 返回受影响都行数
	 */
	public int execute(String sql, String... params){
		PreparedStatement ps = null;
		try {
			ps=this.getPrepStatement(sql, params);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally{
			this.closeRes(null,ps,null);
		}
	}

	/**
	 * 查询返回单个对象转成Map
	 * @param sql
	 * @param args
	 * @return
	 */
	public Map<String,Object> queryForMap(String sql, String... params){
		ResultSet rs = null;
		PreparedStatement ps = null;
		Map<String,Object> retMap = new HashMap();
		try {

			ps = this.getPrepStatement(sql, params);
			rs = ps.executeQuery();
			if (rs != null && rs.next()) {
				retMap = convertToMap(rs);
			}
			return retMap;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			this.closeRes(null,ps,rs);
		}
	}



	/**
	 * 查询返回String
	 * @param sql
	 * @param params
	 * @return
	 */
	public String queryForString(String sql, String... params){
		String retMsg = "";
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = this.getPrepStatement(sql, params);
			rs = ps.executeQuery();
			if (rs != null && rs.next()) {
				retMsg = rs.getString(1);
			}
			return retMsg;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			this.closeRes(null,ps,rs);
		}
	}


	/**
	 * 查询返回List每个元素为Map对象
	 * @param sql
	 * @param args
	 * @return
	 */
	public List<Map> queryForList(String sql, String... params){
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = this.getPrepStatement(sql, params);
			rs = ps.executeQuery();
			List<Map> results = new ArrayList();
			while (rs != null && rs.next()) {
				results.add(convertToMap(rs));
			}
			return results;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			this.closeRes(null,ps,rs);
		}
	}

	/**
	 * 初始化数据库连接
	 */
	private void initCon() throws SQLException{
		if(con!=null&&!con.isClosed())return ;
		if(DBUserInfo ==null)throw new RuntimeException("userInfo is reqired!!!");

		try {
			synchronized (this) {
				if(con!=null&&!con.isClosed())return ;
				Class.forName(DBUserInfo.getDriver());
				DriverManager.setLoginTimeout(15*1000);
				con=DriverManager.getConnection(
						DBUserInfo.getUrl(), DBUserInfo.getUser(), DBUserInfo.getPasswd());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取语句包装对象
	 */
	private PreparedStatement getPrepStatement(String sql, String... params){
		PreparedStatement ps = null;
		try {
			initCon();
			ps=con.prepareStatement(sql);
			if(params!=null)
				for(int i=0;i<params.length;i++){
					ps.setString(i+1, params[i]);
				}
			return ps;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 用户信息
	 * @author Qian
	 */
	public static class DBUserInfo {
		private String driver;  // 数据库驱动对象
		private String url;  	// 数据库连接地址(数据库名)
		private String user;  	// 登陆用户名
		private String passwd; 	// 登陆密码

		public DBUserInfo() {}
		public DBUserInfo(String user, String passwd, String url, String driver){
			this.user=user;
			this.passwd=passwd;
			this.url=url;
			this.driver=driver;
		}

		@Override
		public String toString() {
			return "[user:"+user+", passwd:"+passwd+", url:"+url+", driver:"+driver+"]";
		}

		public void setUrl(String url) {this.url = url;}
		public void setUser(String user) {this.user = user;}
		public void setDriver(String driver) {this.driver = driver;}
		public void setPasswd(String passwd) {this.passwd = passwd;}
		public String getUrl() {return url;}
		public String getUser() {return user;}
		public String getDriver() {return driver;}
		public String getPasswd() {return passwd;}

	}


	/**
	 * 关闭连接
	 */
	private void closeRes(Connection con,Statement ps,ResultSet rs){
		if(rs!=null)
			try {rs.close();} catch (SQLException e){}
		if(ps!=null)
			try {ps.close();} catch (SQLException e){}
		if(con!=null)
			try {con.close();}catch (SQLException e){}
	}

	/**
	 * 结果转为Map
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private Map<String,Object> convertToMap(ResultSet rs) throws SQLException {
		Map<String,Object> retMap = new HashMap();
		ResultSetMetaData meatData = rs.getMetaData();
		for (int i = 1; i <= meatData.getColumnCount(); i++) {
			Object value = null;
			String name = meatData.getColumnLabel(i).toLowerCase();
			int type = meatData.getColumnType(i);
			if ((Types.TIME == type || Types.TIMESTAMP == type)&&rs.getTimestamp(name)!=null) {
				value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(rs.getTimestamp(name).getTime());
			} else if (Types.VARCHAR == type) {
				value = rs.getString(name);
			} else if (Types.INTEGER == type) {
				value = String.valueOf(rs.getInt(name));
			} else if (Types.NUMERIC == type || Types.BIGINT == type) {
				value = rs.getBigDecimal(name);
				value = value != null ? value.toString() : null;
			} else if (Types.BLOB == type) {
				Blob blob = rs.getBlob(name);
				if (blob != null && blob.length() > 0)
					value = blob.getBytes(1, (int) blob.length());
			} else if (Types.CLOB == type) {
				Clob clob = rs.getClob(name);
				if (clob != null && clob.length() > 0)
					value = Clob2String(clob);
			} else {
				value = rs.getObject(name);
			}
			retMap.put(name, value);
		}
		return retMap;
	}

	/**
	 *  Clob转换成String 的方法
	 * @param clob
	 * @return
	 */
	private String Clob2String(Clob clob) {
		String content = null;
		StringBuffer stringBuf = new StringBuffer();
		try {
			int length = 0;
			Reader inStream = clob.getCharacterStream(); // 取得大字侧段对象数据输出流
			char[] buffer = new char[75];
			while ((length = inStream.read(buffer)) != -1){
				for (int i = 0; i < length; i++) {
					stringBuf.append(buffer[i]);
				}
			}
			inStream.close();
			content = stringBuf.toString();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return content;
	}

	public DBUserInfo getDBUserInfo() {
		return DBUserInfo;
	}

	/**
	 * 并不确保一定执行
	 * 测试用途的足够了
	 */
	@Override
	protected void finalize() throws Throwable {
		this.closeRes(con,null,null);
		super.finalize();
	}

	/**
	 * 是否空字符串
	 * @param str
	 * @return
	 */
	public static boolean isBlankStr(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}  
