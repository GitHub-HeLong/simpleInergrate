package dao;

import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mysql {

	private static final Logger LOGGER = LoggerFactory.getLogger(Mysql.class);

	private String driver;
	private String url;
	private String user;
	private String password;
	private String connectionName;
	private DB db;

	public Mysql(String driver, String url, String user, String password,
			String connectionName) {
		super();
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
		this.connectionName = connectionName;
	}

	// 打开数据库连接
	public boolean openDatabase() {
		try {
			new DB(connectionName).open(driver, url, user, password);
			return true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}

	// 关闭数据库连接
	public boolean closeDatabase() {
		try {
			if (new DB(connectionName).hasConnection()) {
				new DB(connectionName).close();
			}
			return true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}

	// 操作数据库对象
	public DB getDb() {
		if (db == null) {
			db = new DB(connectionName);
		}
		return db;
	}

	public void analyzerPlatformInfo() {
		String sql = "select * from imm_roledev Limit 0,10";
		List<Map> result = getDb().findAll(sql);
		LOGGER.info(result.toString());
	}
}
