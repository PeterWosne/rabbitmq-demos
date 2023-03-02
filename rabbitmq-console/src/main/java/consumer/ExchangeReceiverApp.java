package consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class ExchangeReceiverApp {

    private static final String EXCHANGER_NAME = "broadcast_exchanger";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGER_NAME, "key");
        System.out.println(" [x] Waiting for messages");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received'" + message + "'");
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
