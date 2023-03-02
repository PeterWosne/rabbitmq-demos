package consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class SimpleReceiverApp {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false,false, null);
        System.out.println(" [x] Waiting for messages");

        //deliverCallback это обработчик сообщений
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}
// когда сообщение попадает в очередь, из этой очереди его может забрать только один consumer, поэтому для широковещательной рассылки нужен
// след подход -> не создавать очереди на стороне сервера, а создать их на стороне потребителя, для каждого своя
