package consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class DoubleDirectReceiverApp {

    private final static String EXCHANGE_NAME = "double_direct";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queue = channel.queueDeclare().getQueue();
        System.out.println("Queue name: " + queue);

        //делаем бинд на одну очередь но будет сразу 2 routingKey
        channel.queueBind(queue, EXCHANGE_NAME, "java");
        channel.queueBind(queue, EXCHANGE_NAME, "c++");
        System.out.println(" [x] Waiting for messages");

        DeliverCallback deliverCallback = (producerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received: '" + message + "'");
        };

        channel.basicConsume(queue, true, deliverCallback, consumerTag -> { });
    }
}
