package activeMQ;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//MQ操作队列
public class OperationMQ {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OperationMQ.class);

	private static ConnectionFactory connectionFactory; // ConnectionFactory--连接工厂，JMS用它创建连接
	// Provider 的连接
	private static Connection connection = null; // Connection ：JMS 客户端到JMS
	private static Session session; // Session： 一个发送或接收消息的线程
	private static Destination destination; // Destination ：消息的目的地;消息发送给谁.

	private static MessageProducer producer; // MessageProducer：消息发送者

	private static MessageConsumer consumer;// 消费者，消息接收者

	public OperationMQ(String ip, String queue) {

		// 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
		connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://" + ip + ":61616");

		try { // 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.TRUE,
					Session.AUTO_ACKNOWLEDGE);
			// 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
			destination = session.createQueue(queue);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendMessage(String messages) {
		try {
			// 得到消息生成者【发送者】
			producer = session.createProducer(destination);
			// 设置不持久化，此处学习，实际根据项目决定
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			TextMessage message = session.createTextMessage(messages);
			// 发送消息到目的地方
			LOGGER.info("发送消息:{}", messages);
			producer.send(message);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		closeConnection(connection);
	}

	public static void receiver() {

		try {
			consumer = session.createConsumer(destination);
			while (true) {
				// 设置接收者接收消息的时间，为了便于测试，这里谁定为10s
				TextMessage message = (TextMessage) consumer.receive(10000);
				if (null != message) {
					System.out.println("收到消息" + message.getText());
				} else {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		closeConnection(connection);
	}

	public static void closeConnection(Connection connection) {
		try {
			if (null != connection)
				connection.close();
		} catch (Throwable ignore) {
		}
	}

}
