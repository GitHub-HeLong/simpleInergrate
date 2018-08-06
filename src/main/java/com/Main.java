package com;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import activeMQ.OperationMQ;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import dao.MysqlFactory;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		// 加载配置文件
		File file = new File("config/application.conf");

		Config root = ConfigFactory.parseFile(file);
		Config config = root.getConfig("config");

		MysqlFactory.init(config);

		// MysqlFactory.RES.openDatabase();
		// MysqlFactory.RES.analyzerPlatformInfo();
		// MysqlFactory.RES.closeDatabase();

		// selectInfo();

		OperationMQ operationMQ = new OperationMQ("10.0.17.19", "FirstQueue");
		operationMQ.sendMessage("sendMessage  helong 2");
		// operationMQ.receiver();
	}

	/**
	 * mysql多线程操作
	 */
	private static void selectInfo() {

		ExecutorService executors = MysqlFactory.getExecutors();

		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();

		for (int i = 0; i < 100; i++) {
			// 多线程
			Future<Boolean> future = executors.submit(new Callable<Boolean>() {

				public Boolean call() throws Exception {
					try {
						MysqlFactory.RES.openDatabase();
						MysqlFactory.RES.analyzerPlatformInfo();
						return true;
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						return false;
					} finally {
						MysqlFactory.RES.closeDatabase();
					}
				}
			});
			futures.add(future);
		}

		for (Future<Boolean> future : futures) {
			try {
				future.get();
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (ExecutionException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		MysqlFactory.shutdown();
	}

}
