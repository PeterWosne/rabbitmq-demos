package producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class TaskProducerApp {

    private final static String TASK_EXCHANGER = "task_exchanger";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(TASK_EXCHANGER, BuiltinExchangeType.FANOUT);

            String message = "Task.....";
            //отправляем 20 задач на исполнение
            for (int i = 0; i < 20; i++) {
                channel.basicPublish(TASK_EXCHANGER, "", false,null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent '" + message + "'");
            }
        }
    }
}
