package dao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;

public class MysqlFactory {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MysqlFactory.class);

	/**
	 * 操作数据库对象
	 */
	public static Mysql RES;

	/**
	 * 默认使用一个线程来同步数据
	 */
	public static int THREAD_POOL_SIZE = 1;

	/**
	 * 线程池
	 */
	private static ExecutorService EXECUTORS = null;

	/**
	 * 解析MYSQL配置
	 */
	public static boolean init(Config config) {

		// 加载 mysql 同步的线程池大小
		THREAD_POOL_SIZE = config.getInt("mysql.thread-pool-size");

		// 加载源 mysql 配置
		Config resMysqlCfg = config.getConfig("mysql.res");

		String driver = resMysqlCfg.getString("driver");
		String url = resMysqlCfg.getString("url");
		String user = resMysqlCfg.getString("user");
		String password = resMysqlCfg.getString("password");
		String connectionName = resMysqlCfg.getString("connectionName");

		// 解析源 mysql 配置
		RES = new Mysql(driver, url, user, password, connectionName);
		return true;
	}

	/**
	 * 获取线程池
	 */
	public static ExecutorService getExecutors() {
		EXECUTORS = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		return EXECUTORS;
	}

	/**
	 * 所有任务完成后，关闭并清空线程池
	 */
	public static void shutdown() {
		EXECUTORS.shutdown();
		EXECUTORS = null;
	}

}
